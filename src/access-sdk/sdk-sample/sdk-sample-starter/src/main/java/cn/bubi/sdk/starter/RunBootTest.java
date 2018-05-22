package cn.bubi.sdk.starter;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.utils.blockchain.BlockchainKeyPair;
import cn.bubi.access.utils.blockchain.SecureKeyGenerator;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.impl.CreateAccountOperation;
import cn.bubi.sdk.core.spi.BcOperationService;
import cn.bubi.sdk.core.spi.BcQueryService;
import cn.bubi.sdk.core.transaction.Transaction;
import cn.bubi.sdk.core.transaction.model.TransactionCommittedResult;
import cn.bubi.sdk.core.utils.GsonUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/27 下午6:17.
 */
@SpringBootApplication
public class RunBootTest implements CommandLineRunner{

    public static void main(String[] args){
        SpringApplication.run(RunBootTest.class, args);
    }

    private BcOperationService operationService;
    private BcQueryService queryService;

    public RunBootTest(BcOperationService operationService, BcQueryService queryService){
        this.operationService = operationService;
        this.queryService = queryService;
    }

    private static String address = "a0012ea403227b861289ed5fcedd30e51e85ef7397ebc6";

    @RequestMapping("create")
    public void create(){
        createAccountOperation();
    }

    @Override
    public void run(String... args) throws Exception{

        // 进行查询
        Account account = queryService.getAccount(address);
        System.out.println(GsonUtil.toJson(account));

        // 简单操作
        createAccountOperation();

    }

    /**
     * 创建账户操作
     */
    private void createAccountOperation(){
        try {
            Transaction transaction = operationService.newTransactionByAccountPool();

            BlockchainKeyPair user = SecureKeyGenerator.generateBubiKeyPair();
            System.out.println(GsonUtil.toJson(user));

            CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
                    .buildDestAddress(user.getBubiAddress())
                    .buildScript("function main(input) { /*do what ever you want*/ }")
                    .buildAddMetadata("boot自定义key1", "boot自定义value1").buildAddMetadata("boot自定义key2", "boot自定义value2")
                    // 权限部分
                    .buildPriMasterWeight(15)
                    .buildPriTxThreshold(15)
                    .buildAddPriTypeThreshold(OperationTypeV3.CREATE_ACCOUNT, 8)
                    .buildAddPriTypeThreshold(OperationTypeV3.SET_METADATA, 6)
                    .buildAddPriTypeThreshold(OperationTypeV3.ISSUE_ASSET, 4)
                    .buildAddPriSigner(SecureKeyGenerator.generateBubiKeyPair().getBubiAddress(), 10)
                    .build();

            TransactionCommittedResult result = transaction.buildAddOperation(createAccountOperation)
                    .buildTxMetadata("交易metadata")
                    .commit();

            System.out.println("\n------------------------------------------------");
            System.out.println(GsonUtil.toJson(result));
        } catch (SdkException e) {
            e.printStackTrace();
        }
    }


}
