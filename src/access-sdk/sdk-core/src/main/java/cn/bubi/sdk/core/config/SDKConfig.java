package cn.bubi.sdk.core.config;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;
import cn.bubi.sdk.core.balance.NodeManager;
import cn.bubi.sdk.core.balance.RpcServiceLoadBalancer;
import cn.bubi.sdk.core.balance.model.RpcServiceConfig;
import cn.bubi.sdk.core.event.EventBusService;
import cn.bubi.sdk.core.event.SimpleEventBusService;
import cn.bubi.sdk.core.event.bottom.BlockchainMqHandler;
import cn.bubi.sdk.core.event.bottom.TxFailManager;
import cn.bubi.sdk.core.event.bottom.TxMqHandleProcess;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.pool.SponsorAccountPoolManager;
import cn.bubi.sdk.core.pool.defaults.DefaultSponsorAccountFactory;
import cn.bubi.sdk.core.seq.AbstractSequenceManager;
import cn.bubi.sdk.core.seq.SimpleSequenceManager;
import cn.bubi.sdk.core.seq.redis.DistributedLock;
import cn.bubi.sdk.core.seq.redis.RedisClient;
import cn.bubi.sdk.core.seq.redis.RedisConfig;
import cn.bubi.sdk.core.seq.redis.RedisSequenceManager;
import cn.bubi.sdk.core.spi.BcOperationService;
import cn.bubi.sdk.core.spi.BcOperationServiceImpl;
import cn.bubi.sdk.core.spi.BcQueryService;
import cn.bubi.sdk.core.spi.BcQueryServiceImpl;
import cn.bubi.sdk.core.transaction.sync.TransactionSyncManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/12 下午5:49.
 * 提供基础配置能力
 */
public class SDKConfig{

    private BcOperationService operationService;
    private BcQueryService queryService;

    public void configSdk(SDKProperties sdkProperties) throws SdkException{

        // 解析原生配置参数
        List<RpcServiceConfig> rpcServiceConfigList = Stream.of(sdkProperties.getIps().split(","))
                .map(ip -> {
                    if (!ip.contains(":") || ip.length() < 5) {
                        return null;
                    }
                    return new RpcServiceConfig(ip.split(":")[0], Integer.valueOf(ip.split(":")[1]));
                })
                .filter(Objects:: nonNull).collect(Collectors.toList());

        // 1 配置nodeManager
        NodeManager nodeManager = new NodeManager(rpcServiceConfigList);

        // 2 配置rpcService
        RpcService rpcService = new RpcServiceLoadBalancer(rpcServiceConfigList, nodeManager);

        // 3配置内部消息总线
        EventBusService eventBusService = new SimpleEventBusService();

        // 4 配置mq以及配套设施 可以配置多个节点监听，收到任意监听结果均可处理
        TxFailManager txFailManager = new TxFailManager(rpcService, eventBusService);

        TxMqHandleProcess mqHandleProcess = new TxMqHandleProcess(txFailManager, eventBusService);
        for (String uri : sdkProperties.getEventUtis().split(",")) {
            new BlockchainMqHandler(uri, mqHandleProcess, eventBusService).init();
        }

        // 5 配置seqManager
        AbstractSequenceManager sequenceManager;
        if (sdkProperties.isRedisSeqManagerEnable()) {
            // 使用redis
            RedisConfig redisConfig = new RedisConfig(sdkProperties.getHost(), sdkProperties.getPort(), sdkProperties.getPassword());

            List<RedisConfig> redisConfigs = new ArrayList<>();
            redisConfigs.add(redisConfig);
            RedisClient redisClient = new RedisClient(redisConfigs);
            redisClient.init();
            DistributedLock distributedLock = new DistributedLock(redisClient.getPool());

            sequenceManager = new RedisSequenceManager(rpcService, redisClient, distributedLock);
        } else {
            //        使用内存
            sequenceManager = new SimpleSequenceManager(rpcService);
            sequenceManager.init();
        }


        // 6 配置transactionSyncManager
        TransactionSyncManager transactionSyncManager = new TransactionSyncManager();
        transactionSyncManager.init();

        // 初始化账户池
        SponsorAccountPoolManager sponsorAccountPoolManager = new SponsorAccountPoolManager(new DefaultSponsorAccountFactory());

        // 7 配置事件总线
        eventBusService.addEventHandler(nodeManager);
        eventBusService.addEventHandler(txFailManager);
        eventBusService.addEventHandler(sequenceManager);
        eventBusService.addEventHandler(transactionSyncManager);
        eventBusService.addEventHandler(sponsorAccountPoolManager);

        // 8 初始化spi
        BcOperationService operationService = new BcOperationServiceImpl(sequenceManager, rpcService, transactionSyncManager, nodeManager, txFailManager, sponsorAccountPoolManager);
        if (sdkProperties.isAccountPoolEnable()) {
            sponsorAccountPoolManager.initPool(operationService, sdkProperties.getAddress(), sdkProperties.getPublicKey(), sdkProperties.getPrivateKey(), sdkProperties.getSize(), sdkProperties.getPoolFilepath(), sdkProperties.getMark());
        }

        BcQueryService queryService = new BcQueryServiceImpl(rpcService);

        this.operationService = operationService;
        this.queryService = queryService;
    }

    public BcOperationService getOperationService(){
        return operationService;
    }

    public BcQueryService getQueryService(){
        return queryService;
    }

}
