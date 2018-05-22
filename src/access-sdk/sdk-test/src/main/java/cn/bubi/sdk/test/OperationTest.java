package cn.bubi.sdk.test;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.adaptation.blockchain.bc.response.TransactionHistory;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;
import cn.bubi.access.utils.blockchain.BlockchainKeyPair;
import cn.bubi.access.utils.blockchain.SecureKeyGenerator;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.OperationFactory;
import cn.bubi.sdk.core.operation.impl.CreateAccountOperation;
import cn.bubi.sdk.core.operation.impl.IssueAssetOperation;
import cn.bubi.sdk.core.transaction.Transaction;
import cn.bubi.sdk.core.transaction.model.TransactionBlob;
import cn.bubi.sdk.core.transaction.model.TransactionCommittedResult;
import cn.bubi.sdk.core.utils.GsonUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/27 下午2:27.
 * 所有正常进行的操作测试
 */
public class OperationTest extends TestConfig{

    @Test
    public void query(){

        Account account = getQueryService().getAccount(address);

        LOGGER.info(GsonUtil.toJson(account));

    }

    @Test
    //    @Ignore("redis test")
    public void redisTest1() throws InterruptedException{
        redisTest();
    }

    @Test
    //    @Ignore("redis test")
    public void redisTest2() throws InterruptedException{
        redisTest();
    }

    private void redisTest() throws InterruptedException{
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                createAccount();
            }
        });
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                createAccount();
            }
        });

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

    }

    /**
     * 1创建账户
     */
    @Test
    public void createAccount(){

        Transaction transaction = getOperationService().newTransaction(address);

        BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
        LOGGER.info(GsonUtil.toJson(keyPair));
        try {
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

            // 可以拿到blob,让前端签名
            TransactionBlob blob = transaction
                    .buildAddOperation(createAccountOperation)
                    // 调用方可以在这里设置一个预期的区块偏移量，1个区块偏移量=3秒或1分钟，可以用3s进行推断，最快情况1分钟=20个区块偏移量
                    .buildFinalNotifySeqOffset(Transaction.HIGHT_FINAL_NOTIFY_SEQ_OFFSET)
                    .generateBlob();

            //            try {
            //                // 模拟用户操作等待
            //                TimeUnit.SECONDS.sleep(65);
            //            } catch (InterruptedException e) {
            //                e.printStackTrace();
            //            }

            // 签名完成之后可以继续提交,需要自己维护transaction保存
            TransactionCommittedResult result = transaction
                    .buildAddSigner(publicKey, privateKey)
                    //.buildAddDigest("公钥",new byte[]{}) 可以让前端的签名在这里加进来
                    .commit();
            resultProcess(result, "创建账号状态:");

        } catch (SdkException e) {
            e.printStackTrace();
        }

        Account account = getQueryService().getAccount(keyPair.getBubiAddress());
        LOGGER.info("新建的账号:" + GsonUtil.toJson(account));
        Assert.assertNotNull("新建的账号不能查询到", account);
    }

    /**
     * 通过账户池发起交易
     */
    @Test
    public void createAccountByPool(){

        Transaction transaction = getOperationService().newTransactionByAccountPool();

        BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
        LOGGER.info(GsonUtil.toJson(keyPair));
        try {
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

            TransactionBlob blob = transaction
                    .buildAddOperation(createAccountOperation)
                    // 调用方可以在这里设置一个预期的区块偏移量，1个区块偏移量=3秒或1分钟，可以用3s进行推断，最快情况1分钟=20个区块偏移量
                    .buildFinalNotifySeqOffset(Transaction.HIGHT_FINAL_NOTIFY_SEQ_OFFSET)
                    .generateBlob();


            // 签名完成之后可以继续提交,需要自己维护transaction保存
            TransactionCommittedResult result = transaction
                    //.buildAddDigest("公钥",new byte[]{}) 可以让前端的签名在这里加进来
                    .commit();
            resultProcess(result, "创建账号状态:");

        } catch (SdkException e) {
            e.printStackTrace();
        }

        Account account = getQueryService().getAccount(keyPair.getBubiAddress());
        LOGGER.info("新建的账号:" + GsonUtil.toJson(account));
        Assert.assertNotNull("新建的账号不能查询到", account);
    }

    /**
     * 2,3发行和转移资产操作
     */
    @Test
    public void AssetOperation(){

        String assetCode = "asset-code";
        long amount = 100;
        long transferAmount = 9;
        try {
            BlockchainKeyPair user1 = createAccountOperation();

            Transaction issueTransaction = getOperationService().newTransaction(user1.getBubiAddress());

            issueTransaction
                    .buildAddOperation(new IssueAssetOperation.Builder().buildAmount(amount).buildAssetCode(assetCode).build())
                    .buildAddSigner(user1.getPubKey(), user1.getPriKey())
                    .commit();

            Account account = getQueryService().getAccount(user1.getBubiAddress());
            LOGGER.info("user1资产:" + GsonUtil.toJson(account.getAssets()));
            Assert.assertNotNull("发行资产不能为空", account.getAssets());

            Transaction transferTransaction = getOperationService().newTransaction(user1.getBubiAddress());
            BlockchainKeyPair user2 = createAccountOperation();

            transferTransaction
                    .buildAddOperation(OperationFactory.newPaymentOperation(user2.getBubiAddress(), user1.getBubiAddress(), assetCode, transferAmount))
                    .buildAddSigner(user1.getPubKey(), user1.getPriKey())
                    .commit();


            Account account2 = getQueryService().getAccount(user2.getBubiAddress());
            LOGGER.info("account2:" + GsonUtil.toJson(account2));

            LOGGER.info("user2资产:" + GsonUtil.toJson(account2.getAssets()));
            Assert.assertNotNull("转移资产没有收到", account2.getAssets());
            Assert.assertEquals("转移资产数量错误", 9, account2.getAssets()[0].getAmount());
        } catch (SdkException e) {
            e.printStackTrace();
        }
    }


    /**
     * 4设置和修改metadata
     */
    @Test
    public void updateMetadata(){
        try {
            BlockchainKeyPair user = createAccountOperation();

            String key1 = "boot自定义key1";
            String key2 = "boot自定义key2";

            SetMetadata setMetadata = getQueryService().getAccount(user.getBubiAddress(), key1);
            setMetadata.setValue("这是新设置的value1");


            Transaction updateMetadataTransaction = getOperationService().newTransaction(user.getBubiAddress());
            updateMetadataTransaction
                    .buildAddOperation(OperationFactory.newUpdateSetMetadataOperation(setMetadata))
                    .buildAddSigner(user.getPubKey(), user.getPriKey())
                    .commit();

            Account account = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("修改metadata结果:" + GsonUtil.toJson(account.getMetadatas()));
            Assert.assertTrue("修改metadata1结果,失败", Arrays.stream(account.getMetadatas())
                    .anyMatch(setMetadata1 -> "这是新设置的value1".equals(setMetadata1.getValue())));


            Transaction newMetadataTransaction = getOperationService().newTransaction(user.getBubiAddress());
            newMetadataTransaction
                    .buildAddOperation(OperationFactory.newSetMetadataOperation("newMetadataKey2", "newMetadataValue2"))
                    .buildAddSigner(user.getPubKey(), user.getPriKey())
                    .commit();

            Account account2 = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("新建metadata结果:" + GsonUtil.toJson(account2.getMetadatas()));
            Assert.assertTrue("新建metadata结果,失败", Arrays.stream(account2.getMetadatas())
                    .anyMatch(setMetadata1 -> "newMetadataValue2".equals(setMetadata1.getValue())));


            SetMetadata setMetadata2 = getQueryService().getAccount(user.getBubiAddress(), key2);
            setMetadata2.setValue("这是新设置的value222");

            Transaction updateMetadataTransaction2 = getOperationService().newTransaction(user.getBubiAddress());
            updateMetadataTransaction2
                    .buildAddOperation(OperationFactory.newSetMetadataOperation(setMetadata2.getKey(), setMetadata2.getValue(), setMetadata2.getVersion()))
                    .buildAddSigner(user.getPubKey(), user.getPriKey())
                    .commit();

            Account account3 = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("修改metadata2结果:" + GsonUtil.toJson(account3.getMetadatas()));
            Assert.assertTrue("修改metadata2结果,失败", Arrays.stream(account3.getMetadatas())
                    .anyMatch(setMetadata1 -> "这是新设置的value222".equals(setMetadata1.getValue())));
        } catch (SdkException e) {
            e.printStackTrace();
        }

    }

    /**
     * 5设置/修改权重
     */
    @Test
    public void setSignerWeight(){
        try {
            BlockchainKeyPair user = createAccountOperation();

            BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();


            getOperationService()
                    .newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetSignerWeightOperation(keyPair.getBubiAddress(), 8))
                    .commit(user.getPubKey(), user.getPriKey());

            Account account = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("增加一个签名人权重8:" + GsonUtil.toJson(account.getPriv()));
            Assert.assertTrue("增加一个签名人权重8,失败", account.getPriv().getSigners().stream()
                    .anyMatch(signer -> signer.getAddress().equals(keyPair.getBubiAddress()) && signer.getWeight() == 8));

            Transaction setSignerWeightTransaction = getOperationService().newTransaction(user.getBubiAddress());
            TransactionCommittedResult setSignerWeightResult = setSignerWeightTransaction
                    .buildAddOperation(OperationFactory.newSetSignerWeightOperation(20))
                    .commit(user.getPubKey(), user.getPriKey());

            resultProcess(setSignerWeightResult, "修改权重结果状态:");

            Account account2 = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("修改权重到20:" + GsonUtil.toJson(account2.getPriv()));
            Assert.assertEquals("修改权重到20,失败", 20, account2.getPriv().getMasterWeight());


            getOperationService()
                    .newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetSignerWeightOperation(keyPair.getBubiAddress(), 0))
                    .commit(user.getPubKey(), user.getPriKey());

            Account account3 = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("移除一个签名人:" + GsonUtil.toJson(account3.getPriv()));
            Assert.assertTrue("移除一个签名人,失败", account3.getPriv().getSigners().stream()
                    .noneMatch(signer -> signer.getAddress().equals(keyPair.getBubiAddress())));
        } catch (SdkException e) {
            e.printStackTrace();
        }

    }

    /**
     * 6设置/修改门限
     */
    @Test
    public void setThreshold(){
        try {
            BlockchainKeyPair user = createAccountOperation();
            getOperationService()
                    .newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetThresholdOperation(14))
                    .commit(user.getPubKey(), user.getPriKey());

            Account account = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("更新交易门限到14:" + GsonUtil.toJson(account.getPriv()));
            Assert.assertEquals("更新交易门限到14,失败", 14, account.getPriv().getThreshold().getTxThreshold());

            getOperationService()
                    .newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetThresholdOperation(OperationTypeV3.CREATE_ACCOUNT, 10))
                    .commit(user.getPubKey(), user.getPriKey());

            Account account2 = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("更新创建账号1到10:" + GsonUtil.toJson(account2.getPriv()));
            Assert.assertTrue("更新创建账号1到10,失败", account2.getPriv().getThreshold().getTypeThresholds().stream()
                    .anyMatch(typeThreshold -> typeThreshold.getType() == 1 && typeThreshold.getThreshold() == 10));

            getOperationService()
                    .newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetThresholdOperation(OperationTypeV3.SET_THRESHOLD, 2))
                    .commit(user.getPubKey(), user.getPriKey());

            Account account3 = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("新增设置门限6到2:" + GsonUtil.toJson(account3.getPriv()));
            Assert.assertTrue("新增设置门限6到2,失败", account3.getPriv().getThreshold().getTypeThresholds().stream()
                    .anyMatch(typeThreshold -> typeThreshold.getType() == 6 && typeThreshold.getThreshold() == 2));

        } catch (SdkException e) {
            e.printStackTrace();
        }

    }


    /**
     * 合约调用
     * 不太会写，随便写个
     */
    @Test
    public void invokeContract(){
        try {
            BlockchainKeyPair user = createAccountOperation();
            BlockchainKeyPair user2 = createAccountOperation();

            TransactionCommittedResult result = getOperationService()
                    .newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newInvokeContractOperation(user2.getBubiAddress(), "inputdata"))
                    .commit(user.getPubKey(), user.getPriKey());

            resultProcess(result, "合约调用。。。");

            TransactionHistory transactionHistory = getQueryService().getTransactionHistoryByHash(result.getHash());
            Assert.assertEquals("合约调用失败", 0, transactionHistory.getTransactions()[0].getErrorCode());
        } catch (SdkException e) {
            e.printStackTrace();
        }
    }

}
