package cn.bubi.sdk.core.seq.redis;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;
import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.adaptation.blockchain.exception.BlockchainError;
import cn.bubi.access.utils.spring.StringUtils;
import cn.bubi.sdk.core.event.message.TransactionExecutedEventMessage;
import cn.bubi.sdk.core.event.source.EventSourceEnum;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.seq.AbstractSequenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/14 下午4:04.
 * 基于redis管理seq，实现集群共享
 */
public class RedisSequenceManager extends AbstractSequenceManager{

    private final Logger logger = LoggerFactory.getLogger(RedisSequenceManager.class);

    private RpcService rpcService;
    private RedisClient redisClient;
    private DistributedLock distributedLock;

    public RedisSequenceManager(RpcService rpcService, RedisClient redisClient, DistributedLock distributedLock){
        super(EventSourceEnum.TRANSACTION_NOTIFY.getEventSource(), TransactionExecutedEventMessage.class);
        this.rpcService = rpcService;
        this.redisClient = redisClient;
        this.distributedLock = distributedLock;
    }

    @Override
    public long getSequenceNumber(String address) throws SdkException{
        String lockIdentifier = distributedLock.lockWithTimeout(address, 60 * 1000, 5 * 1000);
        if (StringUtils.isEmpty(lockIdentifier)) {
            throw new SdkException(SdkError.REDIS_ERROR_LOCK_TIMEOUT);
        }

        try {
            Long currentSeq = redisClient.getSeq(address);
            if (currentSeq == null) {
                currentSeq = getSeqByAddress(address);
            }

            long useSeq = currentSeq + 1;
            redisClient.setSeq(address, useSeq);

            return useSeq;
        } finally {
            distributedLock.releaseLock(address, lockIdentifier);
        }
    }

    private long getSeqByAddress(String address) throws SdkException{
        Account account = rpcService.getAccount(address);
        if (account == null) {
            throw new SdkException(BlockchainError.TARGET_NOT_EXIST);
        }
        return account.getNonce();
    }

    @Override
    public void reset(String address){
        long currentSeq;
        try {
            currentSeq = getSeqByAddress(address);
        } catch (SdkException e) {
            logger.error("reset found exception", e);
            return;
        }

        redisClient.setSeq(address, currentSeq);
    }

    @Override
    public void processMessage(TransactionExecutedEventMessage message){
        if (!message.getSuccess()) {
            reset(message.getSponsorAddress());
        }
    }
}
