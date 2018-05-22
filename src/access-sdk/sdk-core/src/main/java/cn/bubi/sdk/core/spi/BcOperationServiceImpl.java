package cn.bubi.sdk.core.spi;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;
import cn.bubi.sdk.core.balance.NodeManager;
import cn.bubi.sdk.core.event.bottom.TxFailManager;
import cn.bubi.sdk.core.pool.SponsorAccountPoolManager;
import cn.bubi.sdk.core.seq.SequenceManager;
import cn.bubi.sdk.core.transaction.Transaction;
import cn.bubi.sdk.core.transaction.sync.TransactionSyncManager;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 下午5:14.
 */
public class BcOperationServiceImpl implements BcOperationService{

    private SequenceManager sequenceManager;
    private RpcService rpcService;
    private TransactionSyncManager transactionSyncManager;
    private NodeManager nodeManager;
    private TxFailManager txFailManager;
    private SponsorAccountPoolManager sponsorAccountPoolManager;

    public BcOperationServiceImpl(SequenceManager sequenceManager, RpcService rpcService, TransactionSyncManager transactionSyncManager, NodeManager nodeManager, TxFailManager txFailManager, SponsorAccountPoolManager sponsorAccountPoolManager){
        this.sequenceManager = sequenceManager;
        this.rpcService = rpcService;
        this.transactionSyncManager = transactionSyncManager;
        this.nodeManager = nodeManager;
        this.txFailManager = txFailManager;
        this.sponsorAccountPoolManager = sponsorAccountPoolManager;
    }

    @Override
    public Transaction newTransactionByAccountPool(){
        return new Transaction(sponsorAccountPoolManager.getRichSponsorAccount(), sequenceManager, rpcService, transactionSyncManager, nodeManager, txFailManager);
    }

    @Override
    public Transaction newTransaction(String sponsorAddress){
        return new Transaction(sponsorAddress, sequenceManager, rpcService, transactionSyncManager, nodeManager, txFailManager);
    }

}
