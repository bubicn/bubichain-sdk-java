package cn.bubi.access.starter;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;
import cn.bubi.sdk.core.balance.NodeManager;
import cn.bubi.sdk.core.event.bottom.TxFailManager;
import cn.bubi.sdk.core.event.bottom.TxMqHandleProcess;
import cn.bubi.sdk.core.pool.SponsorAccountPoolManager;
import cn.bubi.sdk.core.seq.SequenceManager;
import cn.bubi.sdk.core.spi.BcOperationService;
import cn.bubi.sdk.core.spi.BcOperationServiceImpl;
import cn.bubi.sdk.core.spi.BcQueryService;
import cn.bubi.sdk.core.spi.BcQueryServiceImpl;
import cn.bubi.sdk.core.transaction.sync.TransactionSyncManager;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/20 下午6:28.
 */
@Configurable
public class BCAutoConfigurationSpi{

    /**
     * 提供操作功能
     */
    @Bean
    @ConditionalOnMissingBean
    public BcOperationService bcOperationService(SequenceManager bcSequenceManager, RpcService bcRpcService,
                                                 TransactionSyncManager bcTransactionSyncManager, NodeManager bcNodeManager,
                                                 TxFailManager txFailManager, SponsorAccountPoolManager sponsorAccountPoolManager,
                                                 BlockchainProperties blockchainProperties,
                                                 TxMqHandleProcess txMqHandleProcess){
        BcOperationService bcOperationService = new BcOperationServiceImpl(bcSequenceManager, bcRpcService, bcTransactionSyncManager, bcNodeManager, txFailManager, sponsorAccountPoolManager);
        // 这里初始化账户池
        BlockchainProperties.SponsorAccountPoolConfig accountPoolConfig = blockchainProperties.getAccountPool();
        if (accountPoolConfig.isEnable()) {
            sponsorAccountPoolManager.initPool(bcOperationService,
                    accountPoolConfig.getAddress(), accountPoolConfig.getPublicKey(), accountPoolConfig.getPrivateKey(),
                    accountPoolConfig.getPoolSize(), accountPoolConfig.getFilePath(), accountPoolConfig.getSponsorAccountMark());
        }
        return bcOperationService;
    }

    /**
     * 提供查询功能
     */
    @Bean
    @ConditionalOnMissingBean
    public BcQueryService bcQueryService(RpcService bcRpcService){
        return new BcQueryServiceImpl(bcRpcService);
    }

}
