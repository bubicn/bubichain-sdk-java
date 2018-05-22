package cn.bubi.access.starter;

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
import cn.bubi.sdk.core.pool.SponsorAccountFactory;
import cn.bubi.sdk.core.pool.SponsorAccountPoolManager;
import cn.bubi.sdk.core.pool.defaults.DefaultSponsorAccountFactory;
import cn.bubi.sdk.core.seq.AbstractSequenceManager;
import cn.bubi.sdk.core.seq.SequenceManager;
import cn.bubi.sdk.core.seq.SimpleSequenceManager;
import cn.bubi.sdk.core.seq.redis.DistributedLock;
import cn.bubi.sdk.core.seq.redis.RedisClient;
import cn.bubi.sdk.core.seq.redis.RedisSequenceManager;
import cn.bubi.sdk.core.transaction.sync.TransactionSyncManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 自动配置类，详细类说明参考simple项目的config类
 */
@Configuration
@EnableConfigurationProperties(BlockchainProperties.class)
public class BCAutoConfiguration{

    /**
     * 配置组件
     */
    @Bean
    public SponsorAccountPoolManager sponsorAccountPoolManager(SponsorAccountFactory sponsorAccountFactory, EventBusService eventBusService){
        SponsorAccountPoolManager sponsorAccountPoolManager = new SponsorAccountPoolManager(sponsorAccountFactory);
        eventBusService.addEventHandler(sponsorAccountPoolManager);
        return sponsorAccountPoolManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public SponsorAccountFactory sponsorAccountFactory(){
        return new DefaultSponsorAccountFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public NodeManager bcNodeManager(BlockchainProperties blockchainProperties, EventBusService eventBusService) throws SdkException{
        List<RpcServiceConfig> bcRpcServiceConfigs = blockchainProperties.getNode().converterRpcServiceConfig();
        NodeManager nodeManager = new NodeManager(bcRpcServiceConfigs);
        eventBusService.addEventHandler(nodeManager);
        return nodeManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcService bcRpcService(BlockchainProperties blockchainProperties, NodeManager bcNodeManager) throws SdkException{
        return new RpcServiceLoadBalancer(blockchainProperties.getNode().converterRpcServiceConfig(), bcNodeManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public TxFailManager bcTxFailManager(RpcService bcRpcService, EventBusService eventBusService){
        TxFailManager txFailManager = new TxFailManager(bcRpcService, eventBusService);
        eventBusService.addEventHandler(txFailManager);
        return txFailManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public TxMqHandleProcess bcTxMqHandleProcess(BlockchainProperties blockchainProperties, TxFailManager bcTxFailManager, EventBusService eventBusService) throws SdkException{
        TxMqHandleProcess bcTxMqHandleProcess = new TxMqHandleProcess(bcTxFailManager, eventBusService);

        // 初始化mq监听
        List<String> uris = blockchainProperties.getEvent().converterUri();
        for (String uri : uris) {
            new BlockchainMqHandler(uri, bcTxMqHandleProcess, eventBusService).init();
        }

        return bcTxMqHandleProcess;
    }


    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public SequenceManager bcSequenceManager(RpcService bcRpcService, EventBusService bcEventBusService, BlockchainProperties blockchainProperties){
        AbstractSequenceManager sequenceManager;
        if (blockchainProperties.getRedisSeq().isEnable()) {
            RedisClient redisClient = new RedisClient(blockchainProperties.getRedisSeq().getRedis());
            redisClient.init();
            DistributedLock distributedLock = new DistributedLock(redisClient.getPool());
            sequenceManager = new RedisSequenceManager(bcRpcService, redisClient, distributedLock);
        } else {
            sequenceManager = new SimpleSequenceManager(bcRpcService);
        }

        bcEventBusService.addEventHandler(sequenceManager);
        return sequenceManager;
    }

    @Bean(initMethod = "init", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public TransactionSyncManager bcTransactionSyncManager(EventBusService eventBusService){
        TransactionSyncManager transactionSyncManager = new TransactionSyncManager();
        eventBusService.addEventHandler(transactionSyncManager);
        return transactionSyncManager;
    }


    @Bean
    @ConditionalOnMissingBean
    public EventBusService bcEventBusService(){
        return new SimpleEventBusService();
    }

}
