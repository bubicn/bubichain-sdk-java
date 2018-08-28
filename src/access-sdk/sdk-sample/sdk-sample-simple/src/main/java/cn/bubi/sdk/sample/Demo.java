package cn.bubi.sdk.sample;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.adaptation.blockchain.bc.response.TransactionHistory;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;
import cn.bubi.access.utils.blockchain.BlockchainKeyPair;
import cn.bubi.access.utils.blockchain.SecureKeyGenerator;
import cn.bubi.sdk.core.config.SDKConfig;
import cn.bubi.sdk.core.config.SDKProperties;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.OperationFactory;
import cn.bubi.sdk.core.operation.impl.CreateAccountOperation;
import cn.bubi.sdk.core.operation.impl.InvokeContractOperation;
import cn.bubi.sdk.core.operation.impl.IssueAssetOperation;
import cn.bubi.sdk.core.operation.impl.PaymentOperation;
import cn.bubi.sdk.core.operation.impl.SetMetadataOperation;
import cn.bubi.sdk.core.operation.impl.SetSignerWeightOperation;
import cn.bubi.sdk.core.operation.impl.SetThresholdOperation;
import cn.bubi.sdk.core.spi.BcOperationService;
import cn.bubi.sdk.core.spi.BcQueryService;
import cn.bubi.sdk.core.transaction.Transaction;
import cn.bubi.sdk.core.transaction.model.TransactionCommittedResult;
import cn.bubi.sdk.core.utils.GsonUtil;
import cn.bubi.sdk.sample.dto.Parameter;

public class Demo{
	
    protected static Logger LOGGER = LoggerFactory.getLogger(Demo.class);

    protected static final String address = "a00172577091bc045301cb117b8551758bc3004841babd";
    protected static final String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
    protected static final String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";

    protected static BcOperationService operationService;
    protected static BcQueryService queryService;

    @BeforeClass
    public static void configSdk() throws SdkException{

    	String eventUtis = "ws://192.168.6.46:7053";
        String ips = "192.168.6.46:19333";

        SDKConfig config = new SDKConfig();
        SDKProperties sdkProperties = new SDKProperties();
        sdkProperties.setEventUtis(eventUtis);
        sdkProperties.setIps(ips);
        sdkProperties.setAccountPoolEnable(true);
        sdkProperties.setAddress(address);
        sdkProperties.setPublicKey(publicKey);
        sdkProperties.setPrivateKey(privateKey);
        sdkProperties.setSize(20);
        sdkProperties.setMark("test-demo-config");
        sdkProperties.setRedisSeqManagerEnable(false);
        sdkProperties.setHost("192.168.10.73");
        sdkProperties.setPort(10379);
        sdkProperties.setPassword("bubi888");
        config.configSdk(sdkProperties);

        Demo.operationService = config.getOperationService();
        Demo.queryService = config.getQueryService();
    }

	/**
	 * 查询账号
	 */
	@Test
	public void getAccount() {
		String accountAddress = "a00161bb2deb827afc1549568cb8e0af4694ee3ff7bfef"; // 要查询的账户地址
		Account account = getQueryService().getAccount(accountAddress);
		System.out.println(JSON.toJSONString(account));
	}

	/**
	 * 查询账户metadata
	 */
	@Test
	public void getMetadataInfo() {
		String destrAddress = "a00189bbdbcbdce6b15fa51193b541307b5480204fd61a"; // 要查询的账户地址
		String key = "该账户的创建人地址"; // 要查询的metadata的key值
		SetMetadata metadata = getQueryService().getAccount(destrAddress, key);
		System.out.println(JSON.toJSONString(metadata));
	}

	/**
	 * 查询交易
	 */
	@Test
	public void getTransaction() {
		String hash = "0f33abfaac6778028ee71ab66600a14e4b84a0daa4f38bf69ff37d991475bec1"; // 要查询的交易的hash值
		TransactionHistory txInfo = getQueryService().getTransactionHistoryByHash(hash);
		System.out.println(JSON.toJSONString(txInfo).toString());
	}

	/**
	 * 生成密钥对和地址
	 */
	@SuppressWarnings("unused")
	@Test
	public void keyPair() {
		BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
		String address = keyPair.getBubiAddress(); // 生成的区块链地址
		String publicKey = keyPair.getPubKey(); // 生成的公钥
		String privateKey = keyPair.getPriKey(); // 生成的私钥
	}

	/**
	 * 创建普通账户
	 */
	@Test
	public void createSimpleAccount() {
		// 交易发起人地址
		String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";
		String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
		String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";
		// 新建交易对象
		Transaction transaction = getOperationService().newTransaction(initiatorAddress);
		// 生成密钥对
		BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
		LOGGER.info(GsonUtil.toJson(keyPair));
		try {
			// 创建一个生成用户操作
			CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
					.buildDestAddress(keyPair.getBubiAddress()).buildPriMasterWeight(15) // 设置账户自身权重
					.buildPriTxThreshold(15) // 设置账户默认门限
					.build();
			transaction.buildAddOperation(createAccountOperation);

			// 签名完成之后可以继续提交
			TransactionCommittedResult result = transaction.buildAddSigner(publicKey, privateKey).commit(true);
			resultProcess(result, "创建账号状态:");

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建用户时设置详细权限信息
	 */
	@Test
	public void createAccountWithWeight() {
		// 交易发起人地址
		String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";
		String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
		String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";

		Transaction transaction = getOperationService().newTransaction(initiatorAddress);

		BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
		LOGGER.info(GsonUtil.toJson(keyPair));
		try {
			CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
					.buildDestAddress(keyPair.getBubiAddress())
					// 设置权限部分
					.buildPriMasterWeight(15) // 设置账户自身权重
					.buildPriTxThreshold(15) // 设置账户默认门限
					.buildAddPriTypeThreshold(OperationTypeV3.CREATE_ACCOUNT, 8) // 设置创建账户门限
					.buildAddPriTypeThreshold(OperationTypeV3.SET_METADATA, 6) // 设置修改metadata门限
					.buildAddPriTypeThreshold(OperationTypeV3.ISSUE_ASSET, 4) // 设置发行资产的门限
					.buildAddPriTypeThreshold(OperationTypeV3.PAYMENT, 6) // 设置转移资产的门限
					.buildAddPriTypeThreshold(OperationTypeV3.SET_SIGNER_WEIGHT, 6) // 设置权重值的门限
					.buildAddPriTypeThreshold(OperationTypeV3.SET_THRESHOLD, 6) // 设置门限值得门限
					.buildAddPriSigner(SecureKeyGenerator.generateBubiKeyPair().getBubiAddress(), 10) // 设置签名人及其权重
					.build();

			transaction.buildAddOperation(createAccountOperation);
			transaction.buildAddSigner(publicKey, privateKey);
			TransactionCommittedResult result = transaction.commit();
			resultProcess(result, "创建账号状态:");

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建账户时设置metadata
	 */
	@Test
	public void createAccountWithMetadata() {
		// 交易发起人地址
		String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";
		String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
		String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";
		// 新建交易对象
		Transaction transaction = getOperationService().newTransaction(initiatorAddress);
		// 生成密钥对
		BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
		LOGGER.info(GsonUtil.toJson(keyPair));
		try {
			// 创建一个生成用户操作
			CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
					.buildDestAddress(keyPair.getBubiAddress()).buildPriMasterWeight(15) // 设置账户自身权重
					.buildPriTxThreshold(15) // 设置账户默认门限
					.buildAddMetadata("该账户的创建人地址", initiatorAddress) // 设置元数据（用户根据需要自定义）
					.build();

			transaction.buildAddOperation(createAccountOperation);

			transaction.buildAddSigner(publicKey, privateKey);
			TransactionCommittedResult result = transaction.commit();
			resultProcess(result, "创建账号状态:");

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 使用池创建账号
	 */
	@Test
	public void createAccountByPool() {
		// 新建一个交易
		Transaction transaction = getOperationService().newTransactionByAccountPool(); // 使用账户池新建交易，无需提供交易发起人地址

		BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
		try {
			CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
					.buildDestAddress(keyPair.getBubiAddress()).buildPriMasterWeight(15) // 设置账户自身权重
					.buildPriTxThreshold(15) // 设置账户默认门限
					.buildOperationMetadata("操作metadata")// 这个操作应该最后build
					.build();

			transaction.buildAddOperation(createAccountOperation);
			TransactionCommittedResult result = transaction.commit();
			resultProcess(result, "创建账号状态:");

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发行资产操作
	 */
	@Test
	public void AssetOperation() {
		// 发行人的地址，公钥和私钥
		String issuerAddress = "a0011de90e7d77cadd4d1562925320207347b744d1f882";
		String issuerPublicKey = "b0015c43932f9d895ee20009a8cc392496922fa62be2b98eb8b3fba78307ec21e50b29";
		String issuerPrivateKey = "c001d831d395505aac526a2d92a39f5735a98d1a0ca741891068be093fed30694c4c91";

		String issueAssetCode = "NEWCNY"; // 将要发现的资产编码
		long issueAmount = 10000; // 将要发行的资产数量
		try {
			// 创建一个交易
			Transaction issueTransaction = getOperationService().newTransaction(issuerAddress);
			// 创建发行资产操作，设置发行量和资产编码
			IssueAssetOperation issueAssetOperation = OperationFactory.newIssueAssetOperation(issueAssetCode,
					issueAmount);
			issueTransaction.buildAddOperation(issueAssetOperation);
			issueTransaction.buildAddSigner(issuerPublicKey, issuerPrivateKey);
			issueTransaction.commit();

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 转移资产
	 */
	@Test
	public void transactionAsset() {

		String sourceAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 资产转出人
		String targetAddress = "a001f40ba48ddf78d553a4e3ad76ffbe7bfe90e83af364"; // 资产接收人
		String issuerAddress = "a0012face49adff86f2f6259f536284230ae6580ec104d"; // 资产发行方地址

		String sourcePublicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777"; // 资产转出人的公钥
		String sourcePrivateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885"; // 资产转出人的私钥
		// 新建交易对象
		Transaction transferTransaction = getOperationService().newTransaction(sourceAddress);
		String transferAssetCode = "NEWCNY"; // 将要进行转移的资产编码
		long transferAmount = 5; // 将要转移的资产数量
		// 创建转移资产操作，设置转移的资产编码和数量
		try {
			PaymentOperation paymentOperation = OperationFactory.newPaymentOperation(targetAddress, issuerAddress,
					transferAssetCode, transferAmount);
			transferTransaction.buildAddOperation(paymentOperation);
			transferTransaction.buildAddSigner(sourcePublicKey, sourcePrivateKey);
			transferTransaction.commit();
		} catch (SdkException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 修改metadata
	 */
	@Test
	public void updateMetadata() {
		try {
			// 将要修改的metadata所在账号的地址
			String address = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
			String pubkey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3"; // 公钥
			String prikey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a"; // 私钥

			String key1 = "该账户的创建人地址";// 要修改的metadata的key值
			// 获取将要修改的metadata对象
			SetMetadata setMetadata = getQueryService().getAccount(address, key1);
			// 设置修改后的value值
			setMetadata.setValue("a00172577091bc045301cb117b8551758bc3004841babd");

			// 新建交易对象
			Transaction updateMetadataTransaction = getOperationService().newTransaction(address);
			// 创建修改metadata操作对象并进行签名提交
			SetMetadataOperation updataSetMetadataOperation = OperationFactory
					.newUpdateSetMetadataOperation(setMetadata);
			updateMetadataTransaction.buildAddOperation(updataSetMetadataOperation);
			updateMetadataTransaction.buildAddSigner(pubkey, prikey);
			updateMetadataTransaction.commit();

		} catch (SdkException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 新建metadata
	 */
	@Test
	public void addMetadata() {
		String address = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
		String pubkey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3"; // 公钥
		String prikey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a"; // 私钥

		Transaction newMetadataTransaction = getOperationService().newTransaction(address);
		try {
			SetMetadataOperation setMetadataOperation = OperationFactory.newSetMetadataOperation("新建metadata的key",
					"新建metadata的值");
			newMetadataTransaction.buildAddOperation(setMetadataOperation);
			newMetadataTransaction.buildAddSigner(pubkey, prikey);
			newMetadataTransaction.commit();
		} catch (SdkException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 修改自身权重
	 */
	@Test
	public void setWeight() {
		// 被修改的账户地址、公钥、私钥
		String address = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
		String publicKey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3"; // 公钥
		String privateKey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a"; // 私钥
		// 新建一个交易
		Transaction setSignerWeightTransaction = getOperationService().newTransaction(address);

		try {
			SetSignerWeightOperation setSignerWeightOperation = OperationFactory.newSetSignerWeightOperation(20);
			setSignerWeightTransaction.buildAddOperation(setSignerWeightOperation); // 设置权重值
			setSignerWeightTransaction.commit(publicKey, privateKey); // 签名并提交交易

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 增加签名人
	 */
	@Test
	public void addSigner() {
		try {
			// 被增加签名人的账户信息
			String initiatorAddress = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
			String initiatorPublicKey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3";
			String initiatorPrivateKey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a";

			String singerAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 新增的签名人账户地址

			// 新建交易对象，创建操作对象，设置新增签名人的权重值并提交
			Transaction addsingerTransaction = getOperationService().newTransaction(initiatorAddress);
			SetSignerWeightOperation setSignerWeightOperation = OperationFactory
					.newSetSignerWeightOperation(singerAddress, 5);
			addsingerTransaction.buildAddOperation(setSignerWeightOperation).commit(initiatorPublicKey,
					initiatorPrivateKey);

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 移除一个签名人
	 */
	@Test
	public void removeSigner() {
		try {
			// 要修改的账户信息
			String initiatorAddress = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
			String initiatorPublicKey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3";
			String initiatorPrivateKey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a";

			String singerAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 要移除的签名人账户地址

			// 新建交易对象，创建操作对象，设置要要移除的签名人权重为0并提交
			Transaction addsingerTransaction = getOperationService().newTransaction(initiatorAddress);
			SetSignerWeightOperation setSignerWeightOperation = OperationFactory
					.newSetSignerWeightOperation(singerAddress, 0);
			addsingerTransaction.buildAddOperation(setSignerWeightOperation).commit(initiatorPublicKey,
					initiatorPrivateKey);

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改一个签名人的权重
	 */
	@Test
	public void setSigner() {
		try {
			// 要修改的账户信息
			String initiatorAddress = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
			String initiatorPublicKey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3";
			String initiatorPrivateKey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a";

			String singerAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 要修改的签名人账户地址

			// 新建交易对象，创建操作对象，设置要修改的签名人及其修改后的权重值并提交
			Transaction addsingerTransaction = getOperationService().newTransaction(initiatorAddress);
			SetSignerWeightOperation setSignerWeightOperation = OperationFactory
					.newSetSignerWeightOperation(singerAddress, 6);
			addsingerTransaction.buildAddOperation(setSignerWeightOperation).commit(initiatorPublicKey,
					initiatorPrivateKey);

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 更改交易门限
	 */
	@Test
	public void setTXThreshold() {
		// 被更改账户的地址、公钥、私钥
		String address = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
		String pubkey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3"; // 公钥
		String prikey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a"; // 私钥

		try {
			// 新建一个交易
			Transaction setThresholdTransaction = getOperationService().newTransaction(address);
			SetThresholdOperation setThresholdOperation = OperationFactory.newSetThresholdOperation(20); // 设置修改后的默认门限值
			setThresholdTransaction.buildAddOperation(setThresholdOperation);
			setThresholdTransaction.commit(pubkey, prikey); // 签名并提交
		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新增指定操作门限值 CREATE_ACCOUNT(1), // 创建账号 ISSUE_ASSET(2), // 发行资产 PAYMENT(3), //
	 * 转移资产 SET_METADATA(4), // 设置metadata SET_SIGNER_WEIGHT(5), // 设置权重
	 * SET_THRESHOLD(6), // 设置门限
	 */
	@Test
	public void addThreshold() {

		String address = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
		String pubkey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3"; // 公钥
		String prikey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a"; // 私钥

		try {
			Transaction setThresholdOperationTransaction = getOperationService().newTransaction(address);
			SetThresholdOperation setThresholdOperation = OperationFactory
					.newSetThresholdOperation(OperationTypeV3.SET_THRESHOLD, 2);
			setThresholdOperationTransaction.buildAddOperation(setThresholdOperation);
			setThresholdOperationTransaction.commit(pubkey, prikey);
		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 修改指定操作门限值
	 */
	@Test
	public void setTypeThreshold() {

		String address = "a001fddff9c611821ddb77fb40026d15d1846df912996b";
		String pubkey = "b001d75db3aa666ad69df0a8b78f03ae55aaf5b69681b4a011dcaceb478d15144afbc3"; // 公钥
		String prikey = "c001c27675bff1d26f25666ad4e1a9b32d7d3dc6b99adab40988e793192114e3ea423a"; // 私钥

		try {
			Transaction setThresholdOperationTransaction = getOperationService().newTransaction(address);
			SetThresholdOperation setThresholdOperation = OperationFactory
					.newSetThresholdOperation(OperationTypeV3.SET_THRESHOLD, 5);
			setThresholdOperationTransaction.buildAddOperation(setThresholdOperation);
			setThresholdOperationTransaction.commit(pubkey, prikey);
		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 创建一个合约账号，设置合约内容（该设置创建完成后不可修改） 合约内容：发行资产
	 */
	@Test
	public void createAccountWithCContract() {
		String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";
		String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
		String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";
		String singerAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 签名人地址

		Transaction transaction = getOperationService().newTransaction(initiatorAddress);

		BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
		LOGGER.info(GsonUtil.toJson(keyPair));
		try {
			CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
					.buildDestAddress(keyPair.getBubiAddress())
					.buildScript("function main(input) { var transaction =\n" + "{\n" + "  'operations' :\n" + "  [\n"
							+ "    {\n" + "      \"type\" : 2,\n" + "      \"issue_asset\" :\n" + "      {\n"
							+ "        \"amount\" : 1000,\n" + "        \"code\" : \"CNY\"\n" + "      }\n" + "    }\n"
							+ "  ]\n" + "};\n" + "\n" + "var result = callBackDoOperation(transaction); }") // 发行资产
					.buildPriMasterWeight(15) // 设置账户自身权重
					.buildPriTxThreshold(15) // 设置账户默认门限
					.buildAddPriSigner(singerAddress, 15) // 设置签名人及其权重
					.buildOperationMetadata("创建了一个带有合约的账号")// 这个操作应该最后build
					.build();

			transaction.buildAddOperation(createAccountOperation);
			TransactionCommittedResult result = transaction.buildAddSigner(publicKey, privateKey).commit();
			resultProcess(result, "创建账号状态:");

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用合约
	 */
	@Test
	public void invokeContract() {
		try {
			String sourceAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 交易发起人的地址
			String pubkey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777"; // 交易发起人的公钥
			String priKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885"; // 交易发起人的私钥
			String targetAddress = "a001a466727373132017148139369d060a968667693032"; // 将要调用的合约账户地址

			// 新建一个交易
			Transaction InvokeContractTransaction = getOperationService().newTransaction(sourceAddress);
			InvokeContractOperation invokeContractOperation = OperationFactory.newInvokeContractOperation(targetAddress,
					"input");
			InvokeContractTransaction.buildAddOperation(invokeContractOperation); // 设置合约账户地址和input
			InvokeContractTransaction.commit(pubkey, priKey); // 签名并提交交易

		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量设置账户metadata
	 * 
	 * @param parameterList
	 *            被操作人员列表
	 *            注：Parameter参数（address,pubKey,priKey,metadataKey,metadataValue）
	 * @param initiator
	 *            交易发起人
	 * @return
	 */
	@Test
	public void updateMetadataList() {
		String sourceAddress = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c"; // 交易发起人的地址
		String pubkey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912"; // 交易发起人的公钥
		String priKey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432"; // 交易发起人的私钥

		List<Parameter> parameterList = new ArrayList<Parameter>();
		Parameter param1 = new Parameter();
		param1.setAddress("a0014d0a1e94da97d3c53e3a79d909bca3059f84eaef58");
		param1.setPubKey("b0016c1290c015aafde53920ffb58cf0c23502db7a106b4355f81c1a4f7a7ca28db410");
		param1.setPriKey("c00190335216bca0f48b15289854c9c6479ed63d9a5e782e2e2b4909af2a3f4d86535d");
		param1.setMetadataKey("批量增加的param1-metadataKey");
		param1.setMetadataValue("批量增加的param1-metadataValue");

		Parameter param2 = new Parameter();
		param2.setAddress("a0018bff58d5256b745951f02f5ca5f05d28c92ed9a293");
		param2.setPubKey("b0011d0d0329d74481d2bb81525f57578463489ed35af0ba6df308aebc9ff903245d32");
		param2.setPriKey("c00186d479d642c49ff255923349418e09f7a473de8603921192aef75f217f9d3c94f4");
		param2.setMetadataKey("批量增加的param2-metadataKey");
		param2.setMetadataValue("批量增加的param2-metadataValue");

		parameterList.add(param1);
		parameterList.add(param2);

		try {
			// 交易发起人创建交易 initiator
			Transaction newMetadataTransaction = getOperationService().newTransaction(sourceAddress);

			// 循环遍历被操作者 parameter
			for (int i = 0; i < parameterList.size(); i++) {
				// 创建修改metadata操作
				SetMetadataOperation operation = OperationFactory.newSetMetadataOperation(
						parameterList.get(i).getMetadataKey(), parameterList.get(i).getMetadataValue());
				// build目标地址
				operation.setOperationSourceAddress(parameterList.get(i).getAddress());
				newMetadataTransaction.buildAddOperation(operation)
						// 被修改者签名
						.buildAddSigner(parameterList.get(i).getPubKey(), parameterList.get(i).getPriKey());
			}
			// 交易发起人签名并提交交易
			TransactionCommittedResult result = newMetadataTransaction.buildAddSigner(pubkey, priKey).commit();

			System.out.println(result.getHash());
		} catch (SdkException e) {
			e.printStackTrace();
		}
	}

    protected void resultProcess(TransactionCommittedResult result, String debugMessage){
        Assert.assertNotNull("result must not null", result);
        Assert.assertNotNull("交易hash不能为空", result.getHash());
        LOGGER.info(debugMessage + GsonUtil.toJson(result));
    }
	
    protected static BcOperationService getOperationService(){
        return operationService;
    }

    protected static BcQueryService getQueryService(){
        return queryService;
    }
}
