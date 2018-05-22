package cn.bubi.sdk.test;

import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.adaptation.blockchain.exception.BlockchainError;
import cn.bubi.access.utils.blockchain.BlockchainKeyPair;
import cn.bubi.access.utils.blockchain.SecureKeyGenerator;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.OperationFactory;
import cn.bubi.sdk.core.operation.impl.CreateAccountOperation;
import cn.bubi.sdk.core.transaction.Transaction;
import cn.bubi.sdk.core.utils.GsonUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/31 上午10:19.
 * 异常错误接收测试
 * 4对象不存在
 * 2参数错误公钥非法
 * 92资产issue 地址非法
 * 93签名权重不够，达不到操作的门限值
 * 94地址非法
 * 99交易序号错误，nonce错误
 * 102创建账号操作，目标账号已存在
 * 103账户不存在
 * 104支付操作，资产余额不足
 * 144metadata的version版本号不与已有的匹配
 * 152合约语法分析失败
 */
public class FailTest extends TestConfig{


    /**
     * 4 对象不存在，如查询不到账号、TX、区块等
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.TARGET_NOT_EXIST
     */
    @Test(expected = SdkException.class)
    public void targetNotExistTest() throws SdkException{
        try {
            BlockchainKeyPair user = createAccountOperation();


            Transaction setSignerWeightTransaction = getOperationService().newTransaction("aaa");
            setSignerWeightTransaction
                    .buildAddOperation(OperationFactory.newSetSignerWeightOperation(14))
                    .commit(user.getPubKey(), user.getPriKey());
        } catch (SdkException e) {
            processSdkException(e, "4 对象不存在", BlockchainError.TARGET_NOT_EXIST);
        }

    }

    /**
     * 92 资产issue 地址非法
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.ILLEGAL_ASSET
     */
    @Test(expected = SdkException.class)
    public void illegalAssetAddressTest() throws SdkException{

        try {
            BlockchainKeyPair user1 = createAccountOperation();

            Transaction transferTransaction = getOperationService().newTransaction(user1.getBubiAddress());
            BlockchainKeyPair user2 = createAccountOperation();
            transferTransaction
                    .buildAddOperation(OperationFactory.newPaymentOperation(user2.getBubiAddress(), "aaa", "assetCode", 1))
                    .buildAddSigner(user1.getPubKey(), user1.getPriKey())
                    .commit();
        } catch (SdkException e) {
            processSdkException(e, "92 资产issue 地址非法:", BlockchainError.ILLEGAL_ASSET);
        }

    }


    /**
     * 93 签名权重不够，达不到操作的门限值
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.WRONG_SIGNATURE
     */
    @Test(expected = SdkException.class)
    public void wrongSignatureTest() throws SdkException{
        try {
            BlockchainKeyPair user = createAccountOperation();

            Transaction setSignerWeightTransaction = getOperationService().newTransaction(user.getBubiAddress());
            setSignerWeightTransaction
                    .buildAddOperation(OperationFactory.newSetSignerWeightOperation(14))
                    .commit(user.getPubKey(), user.getPriKey());

            Account account2 = getQueryService().getAccount(user.getBubiAddress());
            LOGGER.info("修改权重到14:" + GsonUtil.toJson(account2.getPriv()));

            BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();


            getOperationService()
                    .newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetSignerWeightOperation(keyPair.getBubiAddress(), 10))
                    .commit(user.getPubKey(), user.getPriKey());
        } catch (SdkException e) {
            processSdkException(e, "93 签名权重不够，达不到操作的门限值 SdkException:", BlockchainError.WRONG_SIGNATURE);
        }

    }

    /**
     * 94 地址非法
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.ILLEGAL_ADDRESS
     */
    @Test(expected = SdkException.class)
    public void illegalDescAddressTest() throws SdkException{

        try {
            Transaction transaction = getOperationService().newTransaction(address);

            CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
                    .buildDestAddress("illegal desc address")
                    .buildAddMetadata("boot自定义key1", "boot自定义value1").buildAddMetadata("boot自定义key2", "boot自定义value2")
                    .build();

            transaction.buildAddOperation(createAccountOperation)
                    .buildTxMetadata("交易metadata")
                    .buildAddSigner(publicKey, privateKey)
                    .commit();
        } catch (SdkException e) {
            processSdkException(e, "94 地址非法:", BlockchainError.ILLEGAL_ADDRESS);
        }
    }

    /**
     * 99 交易序号错误，nonce错误
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.TX_WRONG_SEQUENCE_NO
     */
    @Test(expected = SdkException.class)
    @Ignore("通过手工修改nonce确认通过测试")
    public void illegalNonceTest() throws SdkException{
        try {
            createAccountOperation();
        } catch (SdkException e) {
            processSdkException(e, "99 交易序号错误，nonce错误:", BlockchainError.TX_WRONG_SEQUENCE_NO);
        }

    }

    /**
     * 102 创建账号操作，目标账号已存在
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.TARGET_ACCOUNT_EXIST
     */
    @Test(expected = SdkException.class)
    public void descAddressExistTest() throws SdkException{
        try {
            BlockchainKeyPair user = createAccountOperation();
            Transaction transaction = getOperationService().newTransaction(address);

            CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
                    .buildDestAddress(user.getBubiAddress())
                    .build();

            transaction.buildAddOperation(createAccountOperation)
                    .buildAddSigner(publicKey, privateKey)
                    .commit();
        } catch (SdkException e) {
            processSdkException(e, "102 创建账号操作，目标账号已存在:", BlockchainError.TARGET_ACCOUNT_EXIST);
        }

    }

    /**
     * 103 账户不存在
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.TARGET_ACCOUNT_NOT_EXIST
     */
    @Test(expected = SdkException.class)
    public void descAddressNotExistTest() throws SdkException{
        String assetCode = "asset-code";
        long amount = 100;
        long transferAmount = 9;
        try {
            BlockchainKeyPair user1 = createAccountOperation();
            Transaction issueTransaction = getOperationService().newTransaction(user1.getBubiAddress());

            issueTransaction
                    .buildAddOperation(OperationFactory.newIssueAssetOperation(assetCode, amount))
                    .buildAddSigner(user1.getPubKey(), user1.getPriKey())
                    .commit();

            BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();

            Transaction transferTransaction = getOperationService().newTransaction(user1.getBubiAddress());
            transferTransaction
                    .buildAddOperation(OperationFactory.newPaymentOperation(keyPair.getBubiAddress(), user1.getBubiAddress(), assetCode, transferAmount))
                    .buildAddSigner(user1.getPubKey(), user1.getPriKey())
                    .commit();
        } catch (SdkException e) {
            processSdkException(e, "103 账户不存在:", BlockchainError.TARGET_ACCOUNT_NOT_EXIST);
        }

    }

    /**
     * 104 支付操作，资产余额不足
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.ASSET_NO_AMOUNT
     */
    @Test(expected = SdkException.class)
    public void balanceNotEnoughTest() throws SdkException{
        String assetCode = "asset-code";
        long amount = 100;
        long transferAmount = 911;
        try {
            BlockchainKeyPair user1 = createAccountOperation();
            BlockchainKeyPair user2 = createAccountOperation();
            Transaction issueTransaction = getOperationService().newTransaction(user1.getBubiAddress());

            issueTransaction
                    .buildAddOperation(OperationFactory.newIssueAssetOperation(assetCode, amount))
                    .buildAddSigner(user1.getPubKey(), user1.getPriKey())
                    .commit();

            Transaction transferTransaction = getOperationService().newTransaction(user1.getBubiAddress());
            transferTransaction
                    .buildAddOperation(OperationFactory.newPaymentOperation(user2.getBubiAddress(), user1.getBubiAddress(), assetCode, transferAmount))
                    .buildAddSigner(user1.getPubKey(), user1.getPriKey())
                    .commit();
        } catch (SdkException e) {
            processSdkException(e, "104 支付操作，资产余额不足:", BlockchainError.ASSET_NO_AMOUNT);
        }

    }

    /**
     * 144 metadata的version版本号不与已有的匹配
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.ILLEGAL_VERSION
     */
    @Test(expected = SdkException.class)
    public void illegalMetadataVersionTest() throws SdkException{
        try {
            BlockchainKeyPair user = createAccountOperation();
            getOperationService().newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetMetadataOperation("boot自定义key1", "vvvv", 0))
                    .buildAddSigner(user.getPubKey(), user.getPriKey())
                    .commit();


            getOperationService().newTransaction(user.getBubiAddress())
                    .buildAddOperation(OperationFactory.newSetMetadataOperation("boot自定义key1", "vvvv", 1))
                    .buildAddSigner(user.getPubKey(), user.getPriKey())
                    .commit();

        } catch (SdkException e) {
            processSdkException(e, "144 metadata的version版本号不与已有的匹配:", BlockchainError.ILLEGAL_VERSION);
        }
    }

    /**
     * 152 合约语法分析失败
     *
     * @see cn.bubi.access.adaptation.blockchain.exception.BlockchainError.CONTRACT_SYNTAX_ERROR
     */
    @Test(expected = SdkException.class)
    public void contractInvokeFailTest() throws SdkException{
        try {
            Transaction transaction = getOperationService().newTransaction(address);

            BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
            LOGGER.info(GsonUtil.toJson(keyPair));

            CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
                    .buildDestAddress(keyPair.getBubiAddress())
                    .buildScript("function main(input) { aaaaaaabbb??dsadsa }")
                    .build();

            transaction.buildAddOperation(createAccountOperation)
                    .buildAddSigner(publicKey, privateKey)
                    .commit();
        } catch (SdkException e) {
            processSdkException(e, "152 合约语法分析失败:", BlockchainError.CONTRACT_SYNTAX_ERROR);
        }
    }

    private void processSdkException(SdkException e, String logMessage, BlockchainError blockchainError) throws SdkException{
        LOGGER.info(logMessage + " SdkException:" + GsonUtil.toJson(e));
        Assert.assertEquals(blockchainError.getDescription() + ",测试失败!", blockchainError.getCode(), e.getErrorCode());
        throw e;
    }


}
