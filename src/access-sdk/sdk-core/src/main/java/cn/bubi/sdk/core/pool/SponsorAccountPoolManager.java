package cn.bubi.sdk.core.pool;

import cn.bubi.sdk.core.event.handle.AbstractEventHandler;
import cn.bubi.sdk.core.event.message.TransactionExecutedEventMessage;
import cn.bubi.sdk.core.event.source.EventSourceEnum;
import cn.bubi.sdk.core.spi.BcOperationService;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/20 上午10:10.
 * 对外提供的账户池操作
 */
public class SponsorAccountPoolManager extends AbstractEventHandler<TransactionExecutedEventMessage>{

    private SponsorAccountFactory sponsorAccountFactory;
    private SponsorAccountPool sponsorAccountPool;

    public SponsorAccountPoolManager(SponsorAccountFactory sponsorAccountFactory){
        super(EventSourceEnum.TRANSACTION_NOTIFY.getEventSource(), TransactionExecutedEventMessage.class);
        this.sponsorAccountFactory = sponsorAccountFactory;
    }

    /**
     * 初始化
     */
    public void initPool(BcOperationService operationService, String address, String publicKey, String privateKey, Integer size, String filePath, String sponsorAccountMark){
        this.sponsorAccountPool = sponsorAccountFactory.initPool(operationService, address, publicKey, privateKey, size, filePath, sponsorAccountMark);
    }

    /**
     * 获取可用发起账户
     */
    public SponsorAccount getRichSponsorAccount(){
        if (sponsorAccountPool == null) {
            throw new IllegalStateException("invoke method getRichSponsorAccount must be method initPool after!");
        }
        return sponsorAccountPool.getRichSponsorAccount();
    }

    /**
     * 通知恢复
     */
    public void notifyRecover(String address){
        if (sponsorAccountPool == null) {
            return;
        }
        sponsorAccountPool.notifyRecover(address);
    }

    @Override
    public void processMessage(TransactionExecutedEventMessage message){
        notifyRecover(message.getSponsorAddress());
    }

}
