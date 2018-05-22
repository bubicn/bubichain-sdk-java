package cn.bubi.sdk.core.balance;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;
import cn.bubi.access.adaptation.blockchain.bc.request.SubTransactionRequest;
import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.adaptation.blockchain.bc.response.Hello;
import cn.bubi.access.adaptation.blockchain.bc.response.TransactionHistory;
import cn.bubi.access.adaptation.blockchain.bc.response.converter.ServiceResponse;
import cn.bubi.access.adaptation.blockchain.bc.response.ledger.Ledger;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;
import cn.bubi.access.adaptation.blockchain.exception.BlockchainException;
import cn.bubi.baas.utils.http.HttpStatusException;
import cn.bubi.baas.utils.http.agent.HttpServiceAgent;
import cn.bubi.baas.utils.http.agent.ServiceEndpoint;
import cn.bubi.sdk.core.balance.model.RpcServiceConfig;
import cn.bubi.sdk.core.balance.model.RpcServiceContent;
import cn.bubi.sdk.core.exception.ExceptionUtil;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/26 上午11:27.
 * 负载访问底层节点
 * 负载策略：取最高区块节点进行访问
 */
public class RpcServiceLoadBalancer implements RpcService{

    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceLoadBalancer.class);

    private RpcService rpcServiceProxy;

    public RpcServiceLoadBalancer(List<RpcServiceConfig> rpcServiceConfigs, NodeManager nodeManager){
        if (rpcServiceConfigs == null || rpcServiceConfigs.isEmpty())
            throw new BlockchainException("Origin RpcServiceConfig at least one!！");

        List<RpcServiceContent> rpcServiceContents = rpcServiceConfigs.stream().map(rpcServiceConfig -> {
            HttpServiceAgent.clearMemoryCache();
            ServiceEndpoint serviceEndpoint = new ServiceEndpoint(rpcServiceConfig.getHost(), rpcServiceConfig.getPort(), rpcServiceConfig.isHttps());
            RpcService rpcService = HttpServiceAgent.createService(RpcService.class, serviceEndpoint);
            return new RpcServiceContent(rpcServiceConfig.getHost(), rpcService);
        }).collect(Collectors.toList());

        this.rpcServiceProxy = (RpcService) Proxy.newProxyInstance(RpcServiceLoadBalancer.class.getClassLoader(), new Class[] {RpcService.class},
                new RpcServiceInterceptor(rpcServiceContents, nodeManager));
    }

    @Override
    public Account getAccount(String address){
        return rpcServiceProxy.getAccount(address);
    }

    @Override
    public Hello hello(){
        return rpcServiceProxy.hello();
    }

    @Override
    public SetMetadata getAccountMetadata(String address, String key){
        return rpcServiceProxy.getAccountMetadata(address, key);
    }

    @Override
    public Ledger getLedger(){
        return rpcServiceProxy.getLedger();
    }

    @Override
    public Ledger getLedgerBySeq(long seq){
        return rpcServiceProxy.getLedgerBySeq(seq);
    }

    @Override
    public String submitTransaction(SubTransactionRequest request){
        return rpcServiceProxy.submitTransaction(request);
    }

    @Override
    public TransactionHistory getTransactionHistoryByAddress(String address){
        return rpcServiceProxy.getTransactionHistoryByAddress(address);
    }

    @Override
    public TransactionHistory getTransactionHistoryBySeq(Long seq, int start, int limit){
        return rpcServiceProxy.getTransactionHistoryBySeq(seq, start, limit);
    }

    @Override
    public TransactionHistory getTransactionHistoryByHash(String hash){
        return rpcServiceProxy.getTransactionHistoryByHash(hash);
    }

    @Override
    public ServiceResponse getTransactionResultByHash(String hash){
        return rpcServiceProxy.getTransactionResultByHash(hash);
    }

    private class RpcServiceInterceptor implements InvocationHandler{

        private Map<String, RpcService> hostRpcMapping = new HashMap<>();
        private NodeManager nodeManager;
        private ExecutorService rpcExecutor = new
                ThreadPoolExecutor(10, 200, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000));
        private int httpStatusTimeout = 408;


        RpcServiceInterceptor(List<RpcServiceContent> originRpcServiceContent, NodeManager nodeManager){
            originRpcServiceContent.forEach(rpcServiceContent -> this.hostRpcMapping.put(rpcServiceContent.getHost(), rpcServiceContent.getRpcService()));
            this.nodeManager = nodeManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable{
            Set<String> usedHosts = new HashSet<>();
            String firstHost = nodeManager.getLastHost();
            usedHosts.add(firstHost);

            Object result;
            String useHost = firstHost;

            while (true) {
                try {
                    result = doInvoke(useHost, method, args);
                    break;
                } catch (Throwable t) {
                    useHost = processThrowable(usedHosts, t);
                }
            }

            return result;
        }

        private String processThrowable(final Set<String> usedHosts, Throwable t) throws Throwable{
            // 对http超时408切换节点访问
            if (t instanceof HttpStatusException && ((HttpStatusException) t).getHttpCode() == httpStatusTimeout) {

                Set<String> allHosts = new HashSet<>(nodeManager.getAllHosts());
                usedHosts.forEach(allHosts:: remove);

                if (allHosts.iterator().hasNext()) {
                    String nowHost = allHosts.iterator().next();
                    LOGGER.info("route http status code 408. now switch to host : " + nowHost);
                    usedHosts.add(nowHost);
                    return nowHost;
                }
            }

            throw t;
        }

        private Object doInvoke(String useHost, Method method, Object[] args) throws Throwable{
            try {
                // 访问底层节点
                RpcService rpcService = hostRpcMapping.get(useHost);
                if (rpcService == null) {
                    LOGGER.warn("router host : " + useHost + " ,but hostRpcMapping not found. hostRpcMapping keys:" + hostRpcMapping.keySet());
                    throw new SdkException(SdkError.EVENT_ERROR_ROUTER_HOST_FAIL);
                }

                LOGGER.info("load balance call rpc service router host : " + useHost + " , method : " + method.getName());

                // 增加了超时连接时间控制
                Future future = rpcExecutor.submit(() -> method.invoke(rpcService, args));
                return future.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                if (e instanceof TimeoutException) {
                    throw new SdkException(SdkError.RPC_INVOKE_ERROR_TIMEOUT);
                }
                throw ExceptionUtil.unwrapThrowable(e);
            }

        }
    }


}
