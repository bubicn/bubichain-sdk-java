package cn.bubi.sdk.core.event.bottom;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;
import cn.bubi.access.adaptation.blockchain.bc.response.Transaction;
import cn.bubi.access.adaptation.blockchain.bc.response.TransactionHistory;
import cn.bubi.access.adaptation.blockchain.exception.BlockchainError;
import cn.bubi.sdk.core.event.EventBusService;
import cn.bubi.sdk.core.event.handle.AbstractEventHandler;
import cn.bubi.sdk.core.event.message.LedgerSeqEventMessage;
import cn.bubi.sdk.core.event.message.TransactionExecutedEventMessage;
import cn.bubi.sdk.core.event.source.EventSourceEnum;
import cn.bubi.sdk.core.exception.SdkError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/26 下午4:34.
 * 交易失败控制器
 */
public class TxFailManager extends AbstractEventHandler<LedgerSeqEventMessage>{

    private static final long SUCCESS = 0;
    private static final long NOT_FOUND = -1;
    private static final long REPEAT_RECEIVE = 3;

    private final Object seqHashMappingLock = new Object();

    private Map<Long, Set<String>> seqHashMapping = new ConcurrentHashMap<>();// seq-hash映射
    private Map<String, Set<TransactionExecutedEventMessage>> hashMessageMapping = new ConcurrentHashMap<>();// hash-message映射

    private static final ExecutorService FAIL_TX_EXECUTOR = new
            ThreadPoolExecutor(5, 200, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000));

    private final RpcService rpcService;
    private final EventBusService eventBusService;

    public TxFailManager(RpcService rpcService, EventBusService eventBusService){
        super(EventSourceEnum.LEDGER_SEQ_INCREASE.getEventSource(), LedgerSeqEventMessage.class);
        this.rpcService = rpcService;
        this.eventBusService = eventBusService;
    }

    @Override
    public void processMessage(LedgerSeqEventMessage message){
        notifySeqUpdate(message.getSeq());
    }


    /**
     * 添加所有的失败事件
     */
    void addFailEventMessage(TransactionExecutedEventMessage message){
        Set<TransactionExecutedEventMessage> messageList = hashMessageMapping.computeIfAbsent(message.getHash(), hash -> new HashSet<>());
        messageList.add(message);
    }

    /**
     * 失败通知，只有当不重复时通知
     */
    void notifyFailEvent(TransactionExecutedEventMessage executedEventMessage){
        synchronized (seqHashMappingLock) {
            if (!(REPEAT_RECEIVE == Integer.valueOf(executedEventMessage.getErrorCode()))) {
                FAIL_TX_EXECUTOR.execute(new FailProcessor(rpcService, executedEventMessage));
            }
        }
    }

    /**
     * 指定seq添加失败hash,存储hash-message和seq-hash,由区块增长通知释放线程
     */
    public void finalNotifyFailEvent(long seq, String hash, SdkError sdkError){
        synchronized (seqHashMappingLock) {
            addFailEventMessage(hash, sdkError);
            Set<String> hashSet = seqHashMapping.computeIfAbsent(seq, key -> new HashSet<>());
            hashSet.add(hash);
        }
    }

    private void addFailEventMessage(String hash, SdkError sdkError){
        TransactionExecutedEventMessage message = new TransactionExecutedEventMessage();
        message.setHash(hash);
        message.setSuccess(false);
        message.setErrorCode(String.valueOf(sdkError.getCode()));
        message.setErrorMessage(sdkError.getDescription());
        addFailEventMessage(message);
    }

    /**
     * 区块增长，释放等待线程
     */
    private void notifySeqUpdate(long newSeq){
        releaseWaitSeqThread(newSeq);
    }

    private void releaseWaitSeqThread(long waitNotifySeq){
        Set<String> waitExecutor = seqHashMapping.remove(waitNotifySeq);

        if (waitExecutor != null && !waitExecutor.isEmpty()) {
            waitExecutor.forEach(hash -> FAIL_TX_EXECUTOR.execute(new FailProcessor(rpcService, hashMessageMapping.remove(hash))));
        }
    }


    /**
     * 失败处理器
     */
    private class FailProcessor implements Runnable{

        private final RpcService rpcService;
        private final Set<TransactionExecutedEventMessage> executedEventMessages;
        private Logger log = LoggerFactory.getLogger(FailProcessor.class);

        private FailProcessor(RpcService rpcService, Set<TransactionExecutedEventMessage> executedEventMessages){
            this.rpcService = rpcService;
            this.executedEventMessages = executedEventMessages == null ? new HashSet<>() : executedEventMessages;
        }

        private FailProcessor(RpcService rpcService, TransactionExecutedEventMessage executedEventMessage){
            this.rpcService = rpcService;
            this.executedEventMessages = new HashSet<>();
            this.executedEventMessages.add(executedEventMessage);
        }

        @Override
        public void run(){
            if (!executedEventMessages.iterator().hasNext()) {
                return;
            }
            TransactionExecutedEventMessage message = executedEventMessages.iterator().next();
            String txHash = message.getHash();
            TransactionHistory transactionHistory = rpcService.getTransactionHistoryByHash(txHash);

            long errorCode = getErrorCode(txHash, transactionHistory);

            // 没有生成交易记录，那么从错误堆中取出最合适的错误信息
            if (errorCode == NOT_FOUND) {
                TransactionExecutedEventMessage failMessage = filterBestMessage();
                eventBusService.publishEvent(EventSourceEnum.TRANSACTION_NOTIFY.getEventSource(), failMessage);
                return;
            }


            // 有交易记录生成，直接取交易记录的状态进行处理
            if (errorCode != SUCCESS) {
                String errorDesc = BlockchainError.getDescription((int) errorCode);
                if (errorDesc == null) {
                    log.warn("errorCode mapping desc not found , errorCode=" + errorCode);
                }

                message.setErrorCode(String.valueOf(errorCode));
                message.setErrorMessage(errorDesc);
                eventBusService.publishEvent(EventSourceEnum.TRANSACTION_NOTIFY.getEventSource(), message);
            }

        }

        private TransactionExecutedEventMessage filterBestMessage(){
            // 选出最合适的错误消息，1由于一定会收到errorCode3，那么它的优先级最低,其它错误有就返回
            for (TransactionExecutedEventMessage message : executedEventMessages) {
                if (Long.valueOf(message.getErrorCode()) != REPEAT_RECEIVE) {
                    return message;
                }
            }
            return executedEventMessages.iterator().next();
        }

        private long getErrorCode(String txHash, TransactionHistory transactionHistory){
            if (transactionHistory != null) {
                Transaction[] transactions = transactionHistory.getTransactions();
                if (transactions != null && transactions.length > 0) {
                    Transaction transaction = transactionHistory.getTransactions()[0];
                    log.debug("FailProcessor:check txHash," + txHash + ",result:" + transaction.getErrorCode());
                    if (txHash.equals(transaction.getHash())) {
                        return transaction.getErrorCode();
                    }
                }
            }
            return NOT_FOUND;
        }
    }

}
