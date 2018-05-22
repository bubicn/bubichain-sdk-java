package cn.bubi.sdk.test;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.utils.blockchain.BlockchainKeyPair;
import cn.bubi.access.utils.blockchain.SecureKeyGenerator;
import cn.bubi.sdk.core.config.SDKConfig;
import cn.bubi.sdk.core.config.SDKProperties;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.impl.CreateAccountOperation;
import cn.bubi.sdk.core.spi.BcOperationService;
import cn.bubi.sdk.core.spi.BcQueryService;
import cn.bubi.sdk.core.transaction.Transaction;
import cn.bubi.sdk.core.transaction.model.TransactionCommittedResult;
import cn.bubi.sdk.core.utils.GsonUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/27 下午2:36.
 * 测试部分主要测试了1负载能力，2正常操作，3部分常见异常
 */
public abstract class TestConfig{

    static Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);

    static final String address = "a0012ea403227b861289ed5fcedd30e51e85ef7397ebc6";
    static final String publicKey = "b001e9fd31a0fc25af3123f67575cdd0c6b8c2192eead9f58728a3fb46accdc0faa67f";
    static final String privateKey = "c0018335e8c3e34cceaa24027207792318bc388bea443b53d5ba9e00e5adb6739bb61b";

    private static BcOperationService operationService;
    private static BcQueryService queryService;

    @BeforeClass
    public static void configSdk() throws SdkException{

        String eventUtis = "ws://192.168.10.100:7053,ws://192.168.10.110:7053,ws://192.168.10.120:7053,ws://192.168.10.130:7053";
        String ips = "192.168.10.100:29333,192.168.10.110:29333,192.168.10.120:29333,192.168.10.130:29333";

        SDKConfig config = new SDKConfig();
        SDKProperties sdkProperties = new SDKProperties();
        sdkProperties.setEventUtis(eventUtis);
        sdkProperties.setIps(ips);
        sdkProperties.setAccountPoolEnable(true);
        sdkProperties.setAddress(address);
        sdkProperties.setPublicKey(publicKey);
        sdkProperties.setPrivateKey(privateKey);
        sdkProperties.setSize(12);
        sdkProperties.setMark("test-demo-config");
        sdkProperties.setRedisSeqManagerEnable(true);
        sdkProperties.setHost("192.168.10.73");
        sdkProperties.setPort(10379);
        sdkProperties.setPassword("bubi888");
        config.configSdk(sdkProperties);

        TestConfig.operationService = config.getOperationService();
        TestConfig.queryService = config.getQueryService();
    }

    /**
     * 创建账户操作
     */
    BlockchainKeyPair createAccountOperation() throws SdkException{

        Transaction transaction = getOperationService().newTransaction(address);

        BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
        LOGGER.info(GsonUtil.toJson(keyPair));

        CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
                .buildDestAddress(keyPair.getBubiAddress())
                .buildScript("function main(input) { /*do what ever you want*/ }")
                .buildAddMetadata("boot自定义key1", "boot自定义value1").buildAddMetadata("boot自定义key2", "boot自定义value2")
                // 权限部分
                .buildPriMasterWeight(15)
                .buildPriTxThreshold(15)
                .buildAddPriTypeThreshold(OperationTypeV3.CREATE_ACCOUNT, 8)
                .buildAddPriTypeThreshold(OperationTypeV3.SET_METADATA, 6)
                .buildAddPriTypeThreshold(OperationTypeV3.ISSUE_ASSET, 4)
                .buildAddPriSigner(SecureKeyGenerator.generateBubiKeyPair().getBubiAddress(), 10)
                .buildOperationMetadata("操作metadata")// 这个操作应该最后build
                .build();

        TransactionCommittedResult result = transaction.buildAddOperation(createAccountOperation)
                .buildTxMetadata("交易metadata")
                .buildAddSigner(publicKey, privateKey)
                .commit();

        resultProcess(result, "创建账号状态:");

        Account account = getQueryService().getAccount(keyPair.getBubiAddress());
        LOGGER.info("新建的账号:" + GsonUtil.toJson(account));
        Assert.assertNotNull("新建的账号不能查询到", account);

        return keyPair;
    }

    void resultProcess(TransactionCommittedResult result, String debugMessage){
        Assert.assertNotNull("result must not null", result);
        Assert.assertNotNull("交易hash不能为空", result.getHash());
        LOGGER.info(debugMessage + GsonUtil.toJson(result));
    }

    static BcOperationService getOperationService(){
        return operationService;
    }

    static BcQueryService getQueryService(){
        return queryService;
    }

}
