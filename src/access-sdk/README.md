#  bubichain-v3 Java SDK

[TOC]

##  概述

本文档详细说明了bubichain-v3 Java SDK的常用接口，使用户可以方便的查询信息和操作区块链。

##  名词解析

创世账户：如果您的区块链刚刚部署完成，那么目前区块链系统中只有一个创世账号

提交交易：向区块链发送写入或修改数据的请求

查询区块链：查询区块链中的数据

账户服务：提供账户相关的查询、修改接口

资产服务：提供资产相关的交易接口

权限服务：提供账户权限设置的接口

合约服务：提供合约相关的创建、调用接口

交易服务：提供交易相关的查询接口

##  环境准备

bubichain-v3 Java SDK需要1.8以上的JDK

##  获取SDK

在pom.xml文件中添加远程库：

```xml
<repositories>
	<repository>
	    <id>nexus-bubi</id>
	    <url>http://maven.bubidev.cn/content/groups/public/</url>
	    <releases>
	        <enabled>true</enabled>
	    </releases>
	    <snapshots>
	        <enabled>true</enabled>
	    </snapshots>
	</repository>
</repositories>
```

然后在pom.xml文件中添加如下配置：

```xml
<dependency>
    <groupId>cn.bubi.access.sdk</groupId>
    <artifactId>sdk-core</artifactId>
    <version>${access-sdk.version}</version>
</dependency>
```

##  配置

###  简单配置

如果不需要使用账户池、Redis服务，可以使用以下简单配置：

```java
// 配置协议地址，如有多个用逗号“，”隔开
String eventUtis = "ws://192.168.6.46:7053"; // tcp协议地址
String ips = "192.168.6.46:19333"; // http协议地址
SDKConfig config = new SDKConfig();
SDKProperties sdkProperties = new SDKProperties();
sdkProperties.setIps(ips);
config.configSdk(sdkProperties);
// 完成配置获得spi
TestConfig.operationService = config.getOperationService();
TestConfig.queryService = config.getQueryService();
```
**注意：如果协议地址配置错误或该地址的链停止运行，SDK会抛出Connection timed out异常。**
###  自定配置

如果需要启用账户池，可以在配置中添加以下配置：

```java
sdkProperties.setAccountPoolEnable(true);  //是否启用账号池（true：启用，flase：不启用）
sdkProperties.setAddress(address);  //创建账号池交易的发起人地址
sdkProperties.setPublicKey(publicKey);  //创建账号池交易的发起人公钥
sdkProperties.setPrivateKey(privateKey);  //创建账号池交易的发起人私钥
sdkProperties.setSize(20);  //账户池中默认账号数量
sdkProperties.setMark("test-demo-config");  //账户池备注信息
```

如果需要启用Redis服务，可以在配置中添加以下配置：

```java
sdkProperties.setRedisSeqManagerEnable(true);  //是否启用Redis（true：启用，flase：不启用）
sdkProperties.setHost("192.168.10.73");  //Redis地址
sdkProperties.setPort(10379);  //Redis的端口号
sdkProperties.setPassword("bubi888");  //Redis密码
```

##  使用方法

这里介绍SDK的使用流程，包含如何生成公私钥地址，如何调用查询接口，以及如何发起交易。

###  生成公私钥地址

> 此接口用于生成区块链账户的公钥、私钥和地址，直接调用SecureKeyGenerator.generateBubiKeyPair接口即可。

代码示例如下：

```Java
@Test
public void generatekeyPair() {
    BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
    System.out.println(keyPair.getBubiAddress());
    System.out.println(keyPair.getPubKey());
    System.out.println(keyPair.getPriKey());
}
```

###  查询

> 此类接口用于查询区块链上的数据，直接调用相应的接口即可，比如，查询账户信息。

调用如下：

```java
@Test
public void getAccountInfo() {
  String destAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; //要查询的账户地址
  Account account = getQueryService().getAccount(destAddress);
  System.out.println(JSON.toJSONString(account));
}
```

###  发起交易

发起交易的过程包含以下几步：

1. 创建交易对象
2. 构建操作
3. 签名交易
4. 提交交易

####  创建交易对象

> 首先创建一个新的Transaction对象。

代码示例如下：

```java
String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";// 交易发起人地址
Transaction transaction = getOperationService().newTransaction(initiatorAddress);
```

> 如果需要使用账户池来发起交易，则不需要提供交易发起人地址，提交交易时账户池会自动为这笔交易提供签名。

代码示例如下：

```java
Transaction transaction = getOperationService().newTransactionByAccountPool(); // 使用账户池新建交易，无需提供交易发起人地址
```

####  构造操作

> 然后构建该交易所要执行的操作，以创建账户为例，需要构建创建用户操作CreateAccountOperation。

代码示例如下：

```java
BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
    .buildDestAddress(keyPair.getBubiAddress())
    .buildOperationMetadata("操作metadata") // 设置该交易的metadata，这个操作应该最后build
    .build();
transaction.buildAddOperation(createAccountOperation);// 将CreateAccountOperation操作add到交易中
```

####  签名交易

> 然后对交易进行签名。

代码示例如下：

```java
transaction.buildAddSigner(publicKey, privateKey);
```

####  提交交易

> 最后向区块链发送交易请求，触发交易的执行。

代码示例如下：

```java
TransactionCommittedResult result = transaction.commit();
```

##  账户服务

以下介绍账户相关的接口，包含创建账户，查询账户信息，查询账户metadata，查询账户资产，新增和修改账户的metadata。

###  创建账户

创建账户需要构建CreateAccountOperation操作。用户可以创建一个简单的账户，也可以在创建账户时就设置签名人、权重以及metadata等账户属性。

代码示例如下：


####  创建一般账户：
> 在创建一个账户时，要设置新账户的地址、公钥、私钥以及最基本的权限信息，即账户自身权重和交易门限值。

```java
@Test
public void createSimpleAccount() {
    //交易发起人地址
    String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";
    String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
    String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";
    // 新建交易对象
    Transaction transaction = getOperationService().newTransaction(initiatorAddress);
    // 生成密钥对
    BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
    LOGGER.info(GsonUtil.toJson(keyPair));
    try {
        //创建一个生成用户操作
        CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
            .buildDestAddress(keyPair.getBubiAddress())
            .buildPriMasterWeight(15) // 设置账户自身权重
            .buildPriTxThreshold(15) // 设置账户默认门限
            .build();
        transaction.buildAddOperation(createAccountOperation);

        // 签名完成之后可以继续提交
        TransactionCommittedResult result = transaction.buildAddSigner(publicKey, privateKey)
            .commit();
        resultProcess(result, "创建账号状态:");

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

####  创建账户的同时设置详细账户权限信息：

> 账户的权限设置包含：账户自身权重值，账户的默认门限值，账户的指定类型操作的门限值以及该账户的签名人及其权重。

```java
@Test
public void createAccountWithWeight() {
    //交易发起人地址
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
            .buildPriTxThreshold(15) // 设置账户交易门限
            .buildAddPriTypeThreshold(OperationTypeV3.CREATE_ACCOUNT, 8) // 设置创建账户门限
            .buildAddPriTypeThreshold(OperationTypeV3.SET_METADATA, 6) // 设置修改metadata门限
            .buildAddPriTypeThreshold(OperationTypeV3.ISSUE_ASSET, 6) // 设置发行资产的门限
            .buildAddPriTypeThreshold(OperationTypeV3.PAYMENT, 6) // 设置转移资产的门限
            .buildAddPriTypeThreshold(OperationTypeV3.SET_SIGNER_WEIGHT, 6) // 设置权重值的门限
            .buildAddPriTypeThreshold(OperationTypeV3.SET_THRESHOLD, 6) // 设置门限值得门限
            .buildAddPriSigner(SecureKeyGenerator.generateBubiKeyPair().getBubiAddress(), 10) //设置签名人及其权重值
            .build();

        transaction.buildAddOperation(createAccountOperation);
        transaction.buildAddSigner(publicKey, privateKey);
        TransactionCommittedResult result = transaction.commit();
        resultProcess(result, "创建账号状态:");

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

####  创建账户的同时设置metadata

> 账户的metadata（元数据）为一个key-value格式的数据，一个账户可以有多个这个的key-value信息，可以用来存储一些备注或标识信息，比如存储创建该账户的账户地址。

```java
@Test
public void createAccountWithMetadata() {
    //交易发起人地址
    String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";
    String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
    String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";
    // 新建交易对象
    Transaction transaction = getOperationService().newTransaction(initiatorAddress);
    // 生成密钥对
    BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
    LOGGER.info(GsonUtil.toJson(keyPair));
    try {
        //创建一个生成用户操作
        CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
            .buildDestAddress(keyPair.getBubiAddress())
            .buildPriMasterWeight(15) // 设置账户自身权重
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
```

####  创建一个合约账户

> 合约账户是一个带有合约信息的账户，合约是一段JavaScript代码,标准(ECMAScript as specified in ECMA-262)。合约的入口函数是main函数，合约代码中必须有main函数的定义。该函数的入参是字符串input，是调用该合约的时候指定的。

```java
@Test
public void createAccountWithCContract() {
    // 发起交易的账户信息
    String initiatorAddress = "a00172577091bc045301cb117b8551758bc3004841babd";
    String publicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777";
    String privateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885";

    Transaction transaction = getOperationService().newTransaction(initiatorAddress);

    BlockchainKeyPair keyPair = SecureKeyGenerator.generateBubiKeyPair();
    LOGGER.info(GsonUtil.toJson(keyPair));
    try {
        CreateAccountOperation createAccountOperation = new CreateAccountOperation.Builder()
            .buildDestAddress(keyPair.getBubiAddress())
            .buildScript("function main(input) { var transaction =\n" +
                         "{\n" +
                         "  'operations' :\n" +
                         "  [\n" +
                         "    {\n" +
                         "      \"type\" : 2,\n" +
                         "      \"issue_asset\" :\n" +
                         "      {\n" +
                         "        \"amount\" : 1000,\n" +
                         "        \"code\" : \"CNY\"\n" +
                         "      }\n" +
                         "    }\n" +
                         "  ]\n" +
                         "};\n" +
                         "\n" +
                         "var result = callBackDoOperation(transaction); }")  //发行资产合约
            .buildOperationMetadata("创建了一个带有合约的账号")// 这个操作应该最后build
            .build();

        transaction.buildAddOperation(createAccountOperation);
        TransactionCommittedResult result = transaction.buildAddSigner(publicKey, privateKey).commit();
        resultProcess(result, "创建账号状态:");

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```



###  查询账户信息

> 此接口用于通过一个账户的区块链地址查询该账户的信息，通过调用BcQueryService的getAccount方法来获取账户的信息。

| 返回对象                 | 方法调用                       | 方法描述                                    |
| ------------------------ | ------------------------------ | ------------------------------------------- |
| [Account](#account对象) | getAccount(String destAddress) | 查询区块链地址为destAddress的账户的账户信息 |

代码示例如下：

```java
@Test
public void getAccount() {
  String destAddress = "a00172577091bc045301cb117b8551758bc3004841babd";//要查询的账户地址
  Account account = getQueryService().getAccount(destAddress);
  System.out.println(JSON.toJSONString(account));
}
```

###  查询账户metadata

> 此接口用于查询一个账户的指定key的metadata的信息，通过调用BcQueryService的getAccount方法来获取。

| 返回对象                        | 方法调用                                  | 方法描述                              |
| ------------------------------- | ----------------------------------------- | ------------------------------------- |
| [SetMetadata](#setmetadata对象) | getAccount(String destAddress,String key) | 查询一个账户的指定key的metadata的信息 |

代码示例如下：

```java
@Test
public void getMetadataInfo() {
    String destrAddress = "a00189bbdbcbdce6b15fa51193b541307b5480204fd61a";//要查询的账户地址
    String key = "该账户的创建人地址";//要查询的metadata的key值
    SetMetadata metadata = getQueryService().getAccount(destrAddress,key);
    System.out.println(JSON.toJSONString(metadata));

}
```

###  新增metadata

> 新增metadata信息需要通过OperationFactoty的newSetMetadataOperation构建SetMetadataOperation操作。

代码示例如下：

```java
@Test
public void addMetadata() {
    // 要新增metadata的账户信息
    String address = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
    String pubkey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912";
    String prikey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432";
    Transaction newMetadataTransaction = getOperationService().newTransaction(address);
    try {
        SetMetadataOperation setMetadataOperation = OperationFactory.newSetMetadataOperation("新建metadata的key", "新建metadata的value");
        newMetadataTransaction.buildAddOperation(setMetadataOperation);
        newMetadataTransaction.buildAddSigner(pubkey, prikey);
        newMetadataTransaction.commit();
    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

###  修改metadata

> 修改metadata信息需要通过OperationFactoty的newUpdateSetMetadataOperation构建SetMetadataOperation操作。

代码示例如下：

```java
@Test
public void updateMetadata() {
    try {
        // 将要修改的metadata所在账号的地址
        String address = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
        String pubkey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912"; // 公钥
        String prikey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432"; // 私钥

        String key1 = "boot自定义key1";//要修改的metadata的key值
        // 获取将要修改的metadata对象
        SetMetadata setMetadata = getQueryService().getAccount(address, key1);
        // 设置修改后的value值
        setMetadata.setValue("修改后key1的值");

        // 新建交易对象
        Transaction updateMetadataTransaction = getOperationService().newTransaction(address);
        // 创建修改metadata操作对象并进行签名提交
        SetMetadataOperation updataSetMetadataOperation = OperationFactory.newUpdateSetMetadataOperation(setMetadata);
        updateMetadataTransaction.buildAddOperation(updataSetMetadataOperation);
        updateMetadataTransaction.buildAddSigner(pubkey, prikey);
        updateMetadataTransaction.commit();

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

##  资产服务

以下介绍资产相关的接口使用，包含发行资产和转移资产。

###  发行资产

> 发行资产是指将数字化的资产登记到区块链网络中，此交易需要通过OperationFactory的newIssueAssetOperation构建IssueAssetOperation操作。

代码示例如下：

```java
@Test
public void IssueAsset() {
    // 发行人的地址，公钥和私钥
    String issuerAddress = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
    String issuerPublicKey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912";
    String issuerPrivateKey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432";
    String issueAssetCode = "assetCode"; // 将要发现的资产编码
    long issueAmount = 10000; // 将要发行的资产数量
    try {
        // 创建一个交易
        Transaction issueTransaction = getOperationService().newTransaction(issuerAddress);
        // 创建发行资产操作，设置发行量和资产编码
        IssueAssetOperation issueAssetOperation = OperationFactory.newIssueAssetOperation(issueAssetCode, issueAmount);
        issueTransaction.buildAddOperation(issueAssetOperation);
        issueTransaction.buildAddSigner(issuerPublicKey, issuerPrivateKey);
        issueTransaction.commit();

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

###   转移资产

> 转移资产是指将账户中的数字化资产转移到区块链中的其他账户中，以实现资产在账户间的流通。此交易需要通过OperationFactory的newPaymentOperation构建PaymentOperation操作。比如现需要将sourceAddress账户中资产编码为NEWCNY，发行人为issuerAddress的资产转移50个到targetAddress账户中。

代码示例如下：

```java
@Test
public void transactionAsset(){

    String sourceAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 资产转出人
    String targetAddress = "a001f40ba48ddf78d553a4e3ad76ffbe7bfe90e83af364"; // 资产接收人
    String issuerAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 资产发行方地址

    String sourcePublicKey = "b001a1806c29423d44953e79c9d0a063391b43f4b6928345ccb7744c1db656e155a777"; // 资产转出人的公钥
    String sourcePrivateKey = "c001df6beb9e047b6024a5300f65ccb2117ac883a1e683d36fa8f6916ec92424e2d885"; // 资产转出人的私钥
    // 新建交易对象
    Transaction transferTransaction = getOperationService().newTransaction(sourceAddress);
    String transferAssetCode = "NEWCNY"; // 将要进行转移的资产编码
    long transferAmount = 50; // 将要转移的资产数量
    // 创建转移资产操作，设置转移的资产编码和数量
    try {
        PaymentOperation paymentOperation = OperationFactory.newPaymentOperation(targetAddress, issuerAddress, transferAssetCode, transferAmount);
        transferTransaction.buildAddOperation(paymentOperation);
        transferTransaction.buildAddSigner(sourcePublicKey, sourcePrivateKey);
        transferTransaction.commit();
    } catch (SdkException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

}
```

##  权限服务

以下介绍账户权限相关的接口使用，包含修改账户自身权重值，增加签名人，修改签名人权重，移除签名人，修改账户交易门限值以及修改指定类型操作的门限值。

###  修改账户自身权重值

> 修改账户自身权重值需要通过OperationFactory的newSetSignerWeightOperation构建SetSignerWeightOperation操作。

代码示例如下：

```java
@Test
public void setWeight() {
    // 被修改的账户地址、公钥、私钥
    String address = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
    String publicKey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912";
    String privateKey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432";
    // 新建一个交易
    Transaction setSignerWeightTransaction = getOperationService().newTransaction(address);

    try {
        SetSignerWeightOperation setSignerWeightOperation = OperationFactory.newSetSignerWeightOperation(200);//设置权重值
        setSignerWeightTransaction.buildAddOperation(setSignerWeightOperation); 
        setSignerWeightTransaction.commit(publicKey, privateKey);  // 签名并提交交易

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

###  增加签名人

> 给账户增加签名人需要通过OperationFactory的newSetSignerWeightOperation构建SetSignerWeightOperation操作。

代码示例如下：

```java
@Test
public void addSigner() {
    try {
        // 被增加签名人的账户信息
        String initiatorAddress = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
        String initiatorPublicKey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912";
        String initiatorPrivateKey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432";

        String singerAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; //新增的签名人账户地址

        // 新建交易对象，创建操作对象，设置新增签名人的权重值并提交
        Transaction addsingerTransaction = getOperationService().newTransaction(initiatorAddress);
        SetSignerWeightOperation setSignerWeightOperation = OperationFactory.newSetSignerWeightOperation(singerAddress, 8);
        addsingerTransaction.buildAddOperation(setSignerWeightOperation).commit(initiatorPublicKey, initiatorPrivateKey);

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

###  修改签名人权重

> 修改一个签名人的权重需要通过OperationFactory的newSetSignerWeightOperation构建SetSignerWeightOperation操作。如果设置一个签名人的权重为0，则这个签名人会被移除。

代码示例如下：

```java
@Test
public void setSigner() {
    try {
        // 要修改的账户信息
        String initiatorAddress = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
        String initiatorPublicKey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912";
        String initiatorPrivateKey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432";

        String singerAddress = "a00172577091bc045301cb117b8551758bc3004841babd"; // 要修改的签名人账户地址

        // 新建交易对象，创建操作对象，设置要修改的签名人及其修改后的权重值并提交
        Transaction addsingerTransaction = getOperationService().newTransaction(initiatorAddress);
        SetSignerWeightOperation setSignerWeightOperation = OperationFactory.newSetSignerWeightOperation(singerAddress, 8);
        addsingerTransaction.buildAddOperation(setSignerWeightOperation).commit(initiatorPublicKey, initiatorPrivateKey);

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

###  修改交易门限值

> 修改交易门限值需要通过OperationFactory的newSetThresholdOperation构建SetThresholdOperation操作。

代码示例如下：

```java
@Test
public void setTXThreshold() {
    // 被更改账户的地址、公钥、私钥
    String address = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
    String pubkey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912";
    String prikey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432";

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
```

###  修改指定类型操作的门限值

> 修改指定类型操作的门限值需要通过OperationFactory的newSetThresholdOperation构建SetThresholdOperation操作。

> 本项目中有以下六种操作类型：

| 代码值 |      枚举名       |     描述     |
| :----: | :---------------: | :----------: |
|   1    |  CREATE_ACCOUNT   |   创建账户   |
|   2    |    ISSUE_ASSET    |   发行资产   |
|   3    |      PAYMENT      |   转移资产   |
|   4    |   SET_METADATA    | 设置metadata |
|   5    | SET_SIGNER_WEIGHT |   设置权重   |
|   6    |   SET_THRESHOLD   |   设置门限   |
代码示例如下：

```java
@Test
public void setTypeThreshold() {

    String address = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c";
    String pubkey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912";
    String prikey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432";

    try {
        Transaction setThresholdOperationTransaction = getOperationService().newTransaction(address);
        SetThresholdOperation setThresholdOperation = OperationFactory.newSetThresholdOperation(OperationTypeV3.SET_THRESHOLD, 2);
        setThresholdOperationTransaction.buildAddOperation(setThresholdOperation);
        setThresholdOperationTransaction.commit(pubkey, prikey);
    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

##  合约服务

###  创建合约账户

> 此服务的功能是创建一个带有合约的账户，其他用户可以触发该账户中的合约操作。此合约一但创建无法进行修改。

#### 什么是合约？

> 合约是一段JavaScript代码，标准(ECMAScript as specified in ECMA-262)。合约的入口函数是main函数，您写的合约代码中必须有main函数的定义。该函数的入参是字符串input，是调用该合约的时候指定的。

####  内置函数和内置变量

> 系统提供了一些全局函数以供用户调用，这些全局函数可以用来获取区块链中的一些信息，也可以用来驱动账户进行交易，比如发行资产、转移资产等。

**注意：用户自定义的函数名和变量名不要与全局函数和内置变量重名，同时也不要使用可能引发数据不一致的Date对象和Math.random()函数**

#### 以下是内置函数介绍：

- 获取账户信息：callBackGetAccountInfo(address);

  代码示例：

  ```javascript
  var accountInfo = callBackGetAccountInfo('a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c'); //address为要查询的账户的地址
  ```

  返回的accountInfo格式如下：

  ```json
  {
    "address": "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c", //该账户的地址
    "assets_hash": "5aef61a8988ce2be1da67cf4b37717748c352b8e4a0bdad2ad0964f80aca0101", //资产hash值
    "contract": null, //合约信息
    "priv": null, //权限信息
    "storage_hash": "e4775fb7fc2a5a06a4bbe0e63f362f8e24ff7752f0259ccd2fe1fc2e6e68781a"
  }
  ```

- 获取账户资产信息：callBackGetAccountAsset(account_address, asset_property);

  代码示例：

  ```javascript
  var account_address = 'a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c'; // 要查询的账户地址
  var asset_property =
  {
    'issuer' : 'a002bbe0b6f547d6bec2c83fb9bb93e75d37c1755f2de6', // 资产发行方地址
    'code' : 'CNY' // 资产编码
  };
  var assetInfo = callBackGetAccountAsset(account_address, asset_property); // 进行查询
  ```

  返回的assetInfo格式如下：

  ```json
  {
    "amount": 1000, // 资产量
    "property": {
      "code": "CNY", // 资产编码
      "issuer": "a002bbe0b6f547d6bec2c83fb9bb93e75d37c1755f2de6" // 资产发行方
    }
  }
  ```

- 获取账户的metadata信息：callBackGetAccountMetaData(account_address, metadata_key);

  代码示例：

  ```javascript
  var account_address = 'a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c'; // 要查询的账户的地址
  var metadata_key = 'theMetadataKey'; // 要查询的metadata的key值
  var metadataValue = callBackGetAccountMetaData(account_address,metadata_key); // 进行查询
  ```

  返回的metadataValue格式如下：

  ```json
  {
      "key":"theMetadataKey", // 查询的metadata的key值
      "value":"hello world", // 查询的metadata的value值
      "version":12 // 查询的metadata的version值
  }
  ```

- 获取区块信息：callBackGetLedgerInfo(ledger_seq);

  代码示例：

  ```javascript
  var ledger = callBackGetLedgerInfo(40); // 查询序号是40的区块的信息
  ```

  返回的ledger的格式如下：

  ```json
  {
      "account_tree_hash": "af05a60772cfd39f3b7838f4032f50450c100dedddf88e0132066688f6ae5c14", // 账号树的hash值
      "consensus_value": {
          "close_time": 1495855656157405, // 区块关闭（打包）时间
          "payload": "240398d89a5efba398fefb0dc194b45abe7b9dbc35326ee8238fff6633371004" //
      },
      "hash": "9f82d8ad1c381e1ce2ce00c559fb2cf3a386d79e9414e92ce3ed809258913384", // 该区块的hash值
      "ledger_sequence": 40, // 该区块的序号
      "ledger_version": 1000, // 该区块的版本号
      "previous_hash": "3ff9b79479d62e7c52f2c0ab08598d219ffd4403bd5c1337764d3591e9b0ba24", // 上一个区块的hash值
      "transaction_tree_hash": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855", // 交易树hash值
      "tx_count": 34359738368 // 区块链截止到该区块的交易量
  }
  ```

- 执行交易：callBackDoOperation(transaction)

  功能是令合约账户执行一笔交易。

  代码示例：

  ```javascript
  var transaction =
  {
    'operations' :
    [
      {
        "type" : 2, // 执行的操作类型
        "issue_asset" : // 资产信息
        {
          "amount" : 1000,
          "code" : "CNY"
        }
      }
    ]
  };

  var result = callBackDoOperation(transaction); // 执行交易，result为true或false
  ```

#### 以下是内置变量介绍：

- thisAddress：该合约账户的地址

- sender：调用该合约的账户地址

- trigger：本合约的交易内容

- triggerIndex：触发本次合约调用的操作的序号

  例如某账号A发起了一笔交易tx0，tx0中第0（从0开始计数）个操作是给某个合约账户转移资产(调用合约), 那么`triggerIndex`的值就是0。

- consensusValue：本次共识数据

  当前块(正在生成的块)的共识数据。ConsensusValue是一个protobuffer对象，其数据结构如下：

  ```protobuf
  message ConsensusValue{
  	TransactionEnvSet txset = 1;
  	int64 close_time = 2;
  	bytes previous_proof = 3;
  	int64 ledger_seq = 4;
  	bytes previous_ledger_hash = 5;
  	LedgerUpgrade ledger_upgrade = 6;
  }
  ```

  常用数据有以下几个：

  ```javascript
  var bar = consensusValue;
  consensusValue.close_time;    /*当前时间,也就是区块生成时间*/
  consensusValue.ledger_seq;     /*当前区块序号*/
  consensusValue.previous_ledger_hash; /*上一个区块hash*/
  ```

###  调用合约

> 此接口用于调用合约代码，需要通过OperationFactory的newInvokeContractOperation构建InvokeContractOperation操作。

代码示例如下：

```java
@Test
public void invokeContract(){
    try {
        String sourceAddress = "a0010d2b1dedbeebdfd29393163ae988fd3e30d70d078c"; // 交易发起人的地址
        String pubkey = "b0013804d12e55eca9ebf8679587a9a7acc081b1524355d01b9d928f4ddad256037912"; // 交易发起人的公钥
        String priKey = "c00159783fc0f7a2adb4d82f4ef53669f138a48ad52af155bda3b18ab9631491b4e432"; // 交易发起人的私钥
        String targetAddress = "a0012face49adff86f2f6259f536284230ae6580ec104d"; // 将要调用的合约账户地址

        // 新建一个交易
        Transaction InvokeContractTransaction = getOperationService().newTransaction(sourceAddress);
        InvokeContractOperation invokeContractOperation = OperationFactory.newInvokeContractOperation(targetAddress, "input");
        InvokeContractTransaction.buildAddOperation(invokeContractOperation); //设置合约账户地址和input
        InvokeContractTransaction.commit(pubkey, priKey); // 签名并提交交易

    } catch (SdkException e) {
        e.printStackTrace();
    }
}
```

##  交易服务

###  查询交易

> 此接口用于通过一个交易的hash值查询该交易的信息，通过调用BcQueryService的getTransactionHistoryByHash方法来获取账户的信息。

| 返回对象                                      | 方法调用                                 | 方法描述                       |
| --------------------------------------------- | ---------------------------------------- | ------------------------------ |
| [TransactionHistory](#transactionhistory对象) | getTransactionHistoryByHash(String hash) | 通过交易的hash值查询交易的信息 |

代码示例如下：

```java
@Test
public void getTransaction() {
    String hash = "0f33abfaac6778028ee71ab66600a14e4b84a0daa4f38bf69ff37d991475bec1"; //要查询的交易的hash值
    TransactionHistory txInfo = getQueryService().getTransactionHistoryByHash(hash);
    System.out.println(JSON.toJSONString(txInfo).toString());
}
```

##  附录

###  响应对象介绍

####  Account对象

| 参数名      | 参数类型      | 参数描述           |
| :---------- | :------------ | :----------------- |
| address     | String        | 账户的区块链地址   |
| assets      | [Asset](#asset对象)[] | 账户资产列表       |
| assetsHash  | String        | 账户资产hash       |
| contract    | [Contract](#contract对象)    | 账户合约           |
| storageHash | String        | 账户区块链存储hash |
| metadatas   | [SetMetadata](#setmetadata对象)[] | 元数据列表         |
| nonce       | long          | 操作序列号         |
| priv        | [Priv](#priv对象)        | 权限信息           |

####  Asset对象

|  参数名  | 参数类型 | 参数描述 |
| :------: | :------: | :------: |
|  amount  |   long   | 资产数量 |
| property | [Property](#property对象) | 资产类型 |

####  Contract对象

| 参数名  | 参数类型 | 参数描述 |
| :-----: | :------: | :------: |
| payload |  String  | 合约内容 |

####  SetMetaData对象

| 参数名  | 参数类型 |     参数描述      |
| :-----: | :------: | :---------------: |
|   key   |  String  |  metadata的key值  |
|  value  |  String  | metadata的value值 |
| version |   long   | metadata的版本号  |

####  Priv对象

|    参数名    |          参数类型           |     参数描述     |
| :----------: | :-------------------------: | :--------------: |
| masterWeight |            long             | 账户的自身权重值 |
|   signers    |        List<[Signer](#singer对象)>         |    签名人列表    |
|  threshold   | [Threshold](#threshold对象) |     门限信息     |

####  Property对象

| 参数名 | 参数类型 |  参数描述  |
| :----: | :------: | :--------: |
|  code  |  String  |  资产编码  |
| issuer |  String  | 资产发行方 |

####  Singer对象

| 参数名  | 参数类型 |    参数描述    |
| :-----: | :------: | :------------: |
| address |  String  |  签名人的地址  |
| weight  |   long   | 签名人的权重值 |

####  Threshold对象

|     参数名     |                 参数类型                  |         参数描述         |
| :------------: | :---------------------------------------: | :----------------------: |
|  txThreshold   |                   long                    |       交易门限权重       |
| typeThresholds | List<[TypeThreshold](#typethreshold对象)> | 各交易类型的门限权重列表 |

#### TypeThreshold对象

|  参数名   | 参数类型 |        参数描述        |
| :-------: | :------: | :--------------------: |
|   type    |   long   |     交易类型的序号     |
| threshold |   long   | 该交易类型的门限权重值 |

#### TransactionHistory对象

|    参数名    |             参数类型              |     参数描述     |
| :----------: | :-------------------------------: | :--------------: |
|  totalCount  |               long                | 查询到的交易数量 |
| transactions | [Transaction](#transaction对象)[] |     交易列表     |

####  Transaction对象

|   参数名    |               参数类型                |           参数描述            |
| :---------: | :-----------------------------------: | :---------------------------: |
|  closeTime  |                 long                  |         交易关闭时间          |
|  errorCode  |                 long                  | 该交易的错误码（0表示无错误） |
|  errorDesc  |                String                 |           错误描述            |
|  ledgerSeq  |                 long                  |     该交易所在的区块序号      |
| signatures  |     [Signature](#signature对象)[]     |      该交易的签名人列表       |
| transaction | [SubTransaction](#subtransaction对象) |         交易内容信息          |
|    hash     |                String                 |        该交易的hash值         |

####  Signature对象

|  参数名   | 参数类型 |  参数描述  |
| :-------: | :------: | :--------: |
| publicKey |  String  | 签名人公钥 |
| signData  |  String  |  签名数据  |

####  SubTransaction对象

|    参数名     |           参数类型            |    参数描述    |
| :-----------: | :---------------------------: | :------------: |
| sourceAddress |            String             | 交易发起人地址 |
|     nonce     |             long              |    交易序号    |
|   metadata    |            String             |   交易元数据   |
|  operations   | [Operation](#operation对象)[] |    操作列表    |

####  Operation对象

|     参数名      |                参数类型                 |     参数描述     |
| :-------------: | :-------------------------------------: | :--------------: |
|  createAccount  |   [CreateAccount](#createaccount对象)   |   创建账户操作   |
| invokeContract  |  [InvokeContract](#invokecontract对象)  |   调用合约操作   |
|   issueAsset    |      [IssueAsset](#issueasset对象)      |   发行资产操作   |
|     payment     |         [Payment](#payment对象)         |   转移资产操作   |
|   setMetadata   |     [SetMetadata](#setmetadata对象)      |  设置元数据操作  |
| setSignerWeight | [SetSignerWeight](#setsignerweight对象) | 设置自身权重操作 |
|  setThreshold   |    [SetThreshold](#setthreshold对象)    |  设置门限值操作  |
|    metadata     |                 String                  |    交易元数据    |
|  sourceAddress  |                 String                  |  交易发起人地址  |
|      type       |                   int                   |   操作类型序号   |

####  CreateAccount对象

|   参数名    |               参数类型                |  参数描述  |
| :---------: | :-----------------------------------: | :--------: |
|  metadata   |                String                 | 操作元数据 |
| destAddress |                String                 |  目标地址  |
|  contract   |       [Contract](#contract对象)       |    合约    |
|  metadatas  | List<[SetMetadata](#setmetadata对象)> | 账户元数据 |
|    priv     |           [Priv](#priv对象)           |    权限    |

####  InvokeContract对象

|     参数名     | 参数类型 |   参数描述   |
| :------------: | :------: | :----------: |
|    metadata    |  String  |  操作元数据  |
|   contractor   |  String  |  合约签订人  |
|     input      |  String  |   合约入参   |
| enable_control | boolean  | 合约可用控制 |

####  IssueAsset对象

|  参数名  | 参数类型 |  参数描述  |
| :------: | :------: | :--------: |
| metadata |  String  | 操作元数据 |
|  amount  |   long   | 资产发行量 |
|   code   |  String  |  资产编码  |

####  Payment对象

|   参数名    |      参数类型       |    参数描述    |
| :---------: | :-----------------: | :------------: |
|  metadata   |       String        |   操作元数据   |
| destaddress |       String        | 资产接收人地址 |
|    asset    | [Asset](#asset对象) |    资产信息    |

####  SetSignerWeight对象

|    参数名    |        参数类型         |    参数描述    |
| :----------: | :---------------------: | :------------: |
| masterWeight |          long           |  账户自身权重  |
|   signers    | [Singer](#singer对象)[] | 账户签名人列表 |

####  SetThreshold对象

|     参数名     |               参数类型                |         参数描述         |
| :------------: | :-----------------------------------: | :----------------------: |
|  txThreshold   |                 long                  |       交易门限权重       |
| typeThresholds | [TypeThreshold](#typethreshold对象)[] | 各交易类型的门限权重列表 |

###  错误码

错误列表如下:

| error_code/错误码 | 枚举名                                 | 错误描述                                                     |
| ----------------- | -------------------------------------- | ------------------------------------------------------------ |
| 0                 | ERRCODE_SUCCESS                        | 操作成功                                                     |
| 1                 | ERRCODE_INTERNAL_ERROR                 | 服务内部错误                                                 |
| 2                 | ERRCODE_INVALID_PARAMETER              | 参数错误                                                     |
| 3                 | ERRCODE_ALREADY_EXIST                  | 对象已存在， 如重复提交交易                                  |
| 4                 | ERRCODE_NOT_EXIST                      | 对象不存在，如查询不到账号、TX、区块等                       |
| 5                 | ERRCODE_TX_TIMEOUT                     | TX 超时，指该 TX 已经被当前节点从 TX 缓存队列去掉，**但并不代表这个一定不能被执行** |
| 20                | ERRCODE_EXPR_CONDITION_RESULT_FALSE    | 指表达式执行结果为 false，意味着该 TX 当前没有执行成功，**但这并不代表在以后的区块不能成功** |
| 21                | ERRCODE_EXPR_CONDITION_SYNTAX_ERROR    | 指表达式语法分析错误，代表该 TX 一定会失败                   |
| 90                | ERRCODE_INVALID_PUBKEY                 | 公钥非法                                                     |
| 91                | ERRCODE_INVALID_PRIKEY                 | 私钥非法                                                     |
| 92                | ERRCODE_ASSET_INVALID                  | 资产issue 地址非法                                           |
| 93                | ERRCODE_INVALID_SIGNATURE              | 签名权重不够，达不到操作的门限值                             |
| 94                | ERRCODE_INVALID_ADDRESS                | 地址非法                                                     |
| 97                | ERRCODE_MISSING_OPERATIONS             | 交易缺失操作                                                 |
| 99                | ERRCODE_BAD_SEQUENCE                   | 交易序号错误，nonce错误                                      |
| 100               | ERRCODE_ACCOUNT_LOW_RESERVE            | 余额不足                                                     |
| 101               | ERRCODE_ACCOUNT_SOURCEDEST_EQUAL       | 源和目的账号相等                                             |
| 102               | ERRCODE_ACCOUNT_DEST_EXIST             | 创建账号操作，目标账号已存在                                 |
| 103               | ERRCODE_ACCOUNT_NOT_EXIST              | 账户不存在                                                   |
| 104               | ERRCODE_ACCOUNT_ASSET_LOW_RESERVE      | 支付操作，资产余额不足                                       |
| 105               | ERRCODE_ACCOUNT_ASSET_AMOUNT_TOO_LARGE | 资产数量过大，超出了int64的范围                              |
| 114               | ERRCODE_OUT_OF_TXCACHE                 | TX 缓存队列已满                                              |
| 120               | ERRCODE_WEIGHT_NOT_VALID               | 权重值不在有效范围内                                         |
| 121               | ERRCODE_THRESHOLD_NOT_VALID            | 门限值不在有效范围内                                         |
| 144               | ERRCODE_INVALID_DATAVERSION            | metadata的version版本号不与已有的匹配（一个版本化的数据库）  |
| 151               | ERRCODE_CONTRACT_EXECUTE_FAIL          | 合约执行失败                                                 |
| 152               | ERRCODE_CONTRACT_SYNTAX_ERROR          | 合约语法分析失败                                             |

### 常见问题

1. **创世账户和普通账户有区别吗？**

   没有区别，创世账户是区块链刚刚部署完时系统中仅有的账户，并没有什么特殊的属性或权限。

2. **close_time的时间戳解析结果为什么不对？**

   需要将时间戳的后三位去掉后再解析，就能得到正确的结果了。

3. **config中配置的address、publicKey，privateKey必须是创世账户吗？**

   不是，其他账户也可以。