# __布比JAVA SDK使用文档__

## 1 用途
该SDK用于与布比底层建立连接，可进行的操作：订阅消息、发送交易、传递消息、接收交易状态

## 2 文档使用说明
1. 了解接口说明的请看目录2 - 5
2. 了解使用步骤的请看目录6 - 8

## 3 依赖的jar包
所依赖的jar包在jar文件夹中寻找，依赖的jar包如下：

1. sadk-3.2.3.0.RELEASE.jar，用于CFCA的签名操作
2. utils-encryption-1.3.16-SNAPSHOT.jar，请看java_encryption.md
3. blockchain-protobuf-3.1.8-SNAPSHOT.jar，布比2.0版依赖的数据结构
4. blockchain-protobuf3-3.1.8-SNAPSHOT.jar，布比3.0版依赖的数据结构
5. eddsa-0.1.0.jar：ed25519签名包
6. bcprov-jdk15on-1.52.jar：证书操作依赖包


### 4 构建BlockChainAdapter对象
BlockChainAdapter blockChainAdapter = new BlockChainAdapter(服务器URL);

例如：
```java
BlockChainAdapter blockChainAdapter = new BlockChainAdapter("ws://127.0.0.1:7053");
```

### 5 绑定回调函数
blockChainAdapter.AddChainMethod(信息类型, 回调函数方法)

注意：绑定消息时注意布比2.0版与布比3.0版的消息路径的区别


#### 5.1 CHAIN_HELLO消息
该消息用于订制消息类型，解析消息时，需要用到ChainStatus数据类型，使用如下：

```java
// 布比2.0版
blockChainAdapter.AddChainMethod(Message.ChainMessageType.CHAIN_HELLO_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//处理 CHAIN_HELLO消息
		Message.ChainStatus chainStatus = Message.ChainStatus.parseFrom(msg);
	}
});
````
或
```java
// 布比3.0版
blockChainAdapter.AddChainMethod(Overlay.ChainMessageType.CHAIN_HELLO_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//处理 CHAIN_HELLO消息
		Overlay.ChainStatus chainStatus = Overlay.ChainStatus.parseFrom(msg);
	}
});
```

#### 5.2 CHAIN_TX_STATUS消息
该消息用于接收交易状态，解析消息时，需要用到ChainTxStatus数据类型，使用如下：
```java
// 布比2.0版
blockChainAdapter.AddChainMethod(Message.ChainMessageType.CHAIN_TX_STATUS_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//处理 CHAIN_TX_STATUS消息
		Message.ChainTxStatus chainTxStatus = Message.ChainTxStatus.parseFrom(msg);
	}
});
```
或
```java
// 布比3.0版
blockChainAdapter.AddChainMethod(Overlay.ChainMessageType.CHAIN_TX_STATUS_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//处理 CHAIN_TX_STATUS消息
		Overlay.ChainTxStatus chainTxStatus = Overlay.ChainTxStatus.parseFrom(msg);
	}
});
```

#### 5.3 CHAIN_PEER_MESSAGE消息
该消息用于节点之间传递消息，解析消息时，需要用到ChainPeerMessage数据类型，使用如下：
```java
// 布比2.0版
blockChainAdapter.AddChainMethod(Message.ChainMessageType.CHAIN_PEER_MESSAGE_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//处理 CHAIN_PEER_MESSAGE消息
		Message.ChainPeerMessage chainPeerMessage = Message.ChainPeerMessage.parseFrom(msg);
	}
});
````
或
```java
// 布比3.0版
blockChainAdapter.AddChainMethod(Overlay.ChainMessageType.CHAIN_PEER_MESSAGE_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//处理 CHAIN_PEER_MESSAGE消息
		Overlay.ChainPeerMessage chainPeerMessage = Overlay.ChainPeerMessage.parseFrom(msg);
	}
});
```

#### 5.4 CHAIN_LEDGER_HEADER_VALUE消息
该消息用于布比程序关闭区块时上传区块状态，需要用到LedgerHeader数据类型，仅用于布比3.0版，使用如下：
```java
chain_message_one_.AddChainMethod(Overlay.ChainMessageType.CHAIN_LEDGER_HEADER_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
    	//处理 CHAIN_LEDGER_HEADER_VALUE消息
    	Chain.LedgerHeader ledger_header = Chain.LedgerHeader.parseFrom(msg);
	}
});
```

#### 5.4 CHAIN_CONTRACT_LOG_VALUE消息
该消息用于布比程序在执行智能合约CallBackLog接口时，上传该日志信息，仅用于布比3.0版，使用如下：
chain_message_one_.AddChainMethod(Overlay.ChainMessageType.CHAIN_CONTRACT_LOG_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//处理 CHAIN_CONTRACT_LOG_VALUE消息
		Overlay.ContractLog contract_log = Overlay.ContractLog.parseFrom(msg);
	}
});

### 6. 发送消息
blockChainAdapter.Send(信息类型， 消息内容);

注意：不同的消息类型，对应的消息的数据格式不同

#### 6.1 CHAIN_HELLO消息
该消息用于订制消息类型，需要用到ChainHello数据类型，使用如下：
```java
// 布比2.0版
Message.ChainHello.Builder chain_hello = Message.ChainHello.newBuilder();
chain_hello.setTimestamp(System.currentTimeMillis());
if (!blockChainAdapter.Send(Message.ChainMessageType.CHAIN_HELLO_VALUE, chain_hello.build().toByteArray())) {
    // 错误输出
}
````
或
```java
// 布比3.0版
Overlay.ChainHello.Builder chain_hello = Overlay.ChainHello.newBuilder();
chain_hello.setTimestamp(System.currentTimeMillis());
if (!blockChainAdapter.Send(Overlay.ChainMessageType.CHAIN_HELLO_VALUE, chain_hello.build().toByteArray())) {
    // 错误输出
}
```

#### 6.2 CHAIN_SUBMITTRANSACTION消息
该消息用于向底层发送交易，需要用到TransactionEnv等数据类型，使用如下：
```java
// 布比2.0版
Message.TransactionEnv.Builder env = Message.TransactionEnv.newBuilder(); 
Message.Transaction.Builder tran = Message.Transaction.newBuilder();
Message.Operation.Builder oper = tran.addOperationsBuilder();
Message.Signature.Builder sign = Message.Signature.newBuilder();
env.setTransaction(tran.build());
env.addSignatures(sign);
if (!blockChainAdapter.Send(Message.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, env.build().toByteArray())) {
	// 错误输出
}
```
或
```java
// 布比3.0版
Chain.TransactionEnv.Builder env = Chain.TransactionEnv.newBuilder(); 
Chain.Transaction.Builder tran = Chain.Transaction.newBuilder();
Chain.Operation.Builder oper = tran.addOperationsBuilder();
Common.Signature.Builder sign = Common.Signature.newBuilder();
if (!blockChainAdapter.Send(Overlay.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, env.build().toByteArray())) {
	// 错误输出
}
```

#### 6.3 CHAIN_PEER_MESSAGE消息
该消息用于底层节点之间传递消息,允许添加多个目标节点地址，需要用到ChainPeerMessage数据类型，使用如下：
```java
// 布比2.0版
Message.ChainPeerMessage.Builder chain_peer_message = Message.ChainPeerMessage.newBuilder(); 
chain_peer_message.setSrcPeerAddr("发行该消息的节点地址");
chain_peer_message.addDesPeerAddrs("要发送到的节点的地址");
chain_peer_message.setData(ByteString.copyFromUtf8("待发送的数据"));
if (!blockChainAdapter.Send(Message.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, chain_peer_message.build().toByteArray())) {
	// 错误输出
}
```
或
```java
// 布比3.0版
Overlay.ChainPeerMessage.Builder chain_peer_message = Overlay.ChainPeerMessage.newBuilder(); 
chain_peer_message.setSrcPeerAddr("发行该消息的节点地址");
chain_peer_message.addDesPeerAddrs("要发送到的节点的地址");
chain_peer_message.setData(ByteString.copyFromUtf8("待发送的数据"));
if (!blockChainAdapter.Send(Overlay.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, chain_peer_message.build().toByteArray())) {
	// 错误输出
}
```

### 7 使用步骤
请配合代码中的chain_test.java使用

#### 7.1 定义BlockChainAdapter对象
```java
private BlockChainAdapter chain_message_one_;
chain_message_one_ = new BlockChainAdapter("ws://127.0.0.1:7053");
```

#### 7.2 绑定回调函数
目前支持的回调类型：
CHAIN_HELLO_VALUE，CHAIN_TX_STATUS_VALUE，CHAIN_PEER_MESSAGE_VALUE

```java
chain_message_one_.AddChainMethod(Overlay.ChainMessageType.CHAIN_HELLO_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//OnChainHello(msg, length);
	}
});
chain_message_one_.AddChainMethod(Overlay.ChainMessageType.CHAIN_TX_STATUS_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//OnChainTxStatus(msg, length);
	}
});
chain_message_one_.AddChainMethod(Overlay.ChainMessageType.CHAIN_PEER_MESSAGE_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
		//OnChainPeerMessage(msg, length);
	}
});
```

#### 7.4 发送交易
参照下面的目录7 “发起交易的例子”

#### 7.5 传递消息
参照下面的目录8 "节点间传递消息的例子"

### 8 发起交易的例子
需要添加依赖JAVA ENCRYPTIOIN:
```pom
<dependency>
	<groupId>cn.bubi.baas.utils</groupId>
	<artifactId>utils-encryption</artifactId>
	<version>1.3.12-SNAPSHOT</version>
</dependency>
```

#### 8.1 创建账号
需要引用JAVA ENCRYPTIOI：
```pom
    <dependency>
        <groupId>cn.bubi.baas.utils</groupId>
        <artifactId>utils-encryption</artifactId>
        <version>1.3.12-SNAPSHOT</version>
    </dependency>
```

##### 8.1.1 布比2.0版
```java
try {
	String privateKey = "privbtZ1Fw5RRWD4ZFR6TAMWjN145zQJeJQxo3EXAABfgBjUdiLHLLHF";
	String address = "bubiV8i2558GmfnBREe87ZagdkKsfeJh5HYjcNpa";
	String httpRequest = "http://127.0.0.1:19333";
	
    // get tx sequence number
	String url = httpRequest + "/getAccount?address=" + address;
	String txSeq = null;
	txSeq = HttpKit.post(url, "");	
	JSONObject tx = JSONObject.parseObject(txSeq);
	String seq_str = tx.getJSONObject("result").getString("tx_seq");
	seq_ = Long.parseLong(seq_str);
    
    // generate new Account address, PrivateKey, publicKey
	BubiKey bubikey_new = new BubiKey(BubiKeyType.ED25519);
	
    // generate transaction
	Message.Transaction.Builder tran = Message.Transaction.newBuilder();
	tran.setSourceAddress("bubiV8i2558GmfnBREe87ZagdkKsfeJh5HYjcNpa");
	tran.setFee(1000);
	tran.setSequenceNumber(seq_ + 1);
	tran.setMetadata(ByteString.copyFromUtf8(String.valueOf(System.currentTimeMillis())));
	
    // add operation
	Message.Operation.Builder oper = tran.addOperationsBuilder();
	oper.setType(Message.Operation.Type.CREATE_ACCOUNT);
    oper.getCreateAccountBuilder().setDestAddress(bubikey_new.getB58Address());
    oper.getCreateAccountBuilder().setInitBalance(20000000);
				
    // add signature	
	Message.Signature.Builder sign = Message.Signature.newBuilder();
	BubiKey bubiKey = new BubiKey(privateKey);
	sign.setPublicKey(bubiKey.getB58PublicKey());
	sign.setSignData(ByteString.copyFrom(bubiKey.sign(tran.build().toByteArray())));
					
	Message.TransactionEnv.Builder env = Message.TransactionEnv.newBuilder(); 
	env.setTransaction(tran.build());
	env.addSignatures(sign);
					
    // send transaction
	chain_message_one_.Send(Message.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, env.build().toByteArray()));
} catch (Exception e1) {
	e1.printStackTrace();
}
```

##### 8.1.1 布比3.0版
```java
try {
    String privateKey = "8df1e02a896ecfcb872dad4dcce75da603b0b906ffe832e53c70a0f94cae54b29c1402cb342e82f3a68ecdf64dfc50a06cebcd610270bd9981c0bc690d6e5e40638e743fadce301878431eed485bfde1";
	String address = "a001ce7e78f5c1d80878bee952b1dc75eb942c9ebed47a";
	String httpRequest = "http://127.0.0.1:19333";
	
	// getAccount
	String getAccount = url + "/getAccount?address=" + address;
	String txSeq = HttpKit.post(getAccount, "");
	JSONObject tx = JSONObject.parseObject(txSeq);
	String seq_str = tx.getJSONObject("result").containsKey("nonce") ? tx.getJSONObject("result").getString("nonce") : "0";
	long nonce = Long.parseLong(seq_str);
			
	// generate new Account address, PrivateKey, publicKey
	if (algorithm == BubiKeyType.CFCA) {
		byte fileData[] = FileUtil.getBytesFromFile(certFile);
		bubikey_new = new BubiKey(certFileType, fileData, password);
	}
	else {
		bubikey_new = new BubiKey(algorithm);
	}
			
	// use src account sign
	BubiKey bubiKey_src = new BubiKey(srcPrivate);
			
			
	// generate transaction
	Transaction.Builder tran = Transaction.newBuilder();
	tran.setSourceAddress(srcAddress);
	tran.setNonce(nonce + 3);
	Operation.Builder oper = tran.addOperationsBuilder();
	oper.setType(Operation.Type.CREATE_ACCOUNT);
	OperationCreateAccount.Builder createAccount = OperationCreateAccount.newBuilder();
	createAccount.setDestAddress(bubikey_new.getB16Address());
	AccountPrivilege.Builder accountPrivilege = AccountPrivilege.newBuilder();
	accountPrivilege.setMasterWeight(1);
	AccountThreshold.Builder accountThreshold = AccountThreshold.newBuilder();
	accountThreshold.setTxThreshold(1);
	accountPrivilege.setThresholds(accountThreshold);
	createAccount.setPriv(accountPrivilege);
	oper.setCreateAccount(createAccount);
			
	Signature.Builder signature  = Signature.newBuilder();
	signature.setPublicKey(bubiKey_src.getB16PublicKey());
	byte[] sign_data = BubiKey.sign(tran.build().toByteArray(), srcPrivate);
	signature.setSignData(ByteString.copyFrom(sign_data));
			
	TransactionEnv.Builder tranEnv = TransactionEnv.newBuilder(); 
	tranEnv.setTransaction(tran.build());
	tranEnv.addSignatures(signature.build());
			
	chain_message_one_.Send(Overlay.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, tranEnv.build().toByteArray());
```

#### 8.2 发行资产
##### 8.2.1 布比2.0版
```java
try {
	String privateKey = "privbtZ1Fw5RRWD4ZFR6TAMWjN145zQJeJQxo3EXAABfgBjUdiLHLLHF";
	String address = "bubiV8i2558GmfnBREe87ZagdkKsfeJh5HYjcNpa";
	String httpRequest = "http://127.0.0.1:19333";
	// get tx sequence number
	String url = httpRequest + "/getAccount?address=" + address;
	String txSeq = null;
	txSeq = HttpKit.post(url, "");

	JSONObject tx = JSONObject.parseObject(txSeq);
	String seq_str = tx.getJSONObject("result").getString("tx_seq");
	seq_ = Long.parseLong(seq_str);
    
    // make transaction
	Message.Transaction.Builder tran = Message.Transaction.newBuilder();
	tran.setSourceAddress(address);
	tran.setFee(1000);
	tran.setSequenceNumber(seq_ + 1);
	tran.setMetadata(ByteString.copyFromUtf8(String.valueOf(System.currentTimeMillis())));
	
    // add operations
	Message.Operation.Builder oper = tran.addOperationsBuilder();
	oper.setType(Message.Operation.Type.ISSUE_ASSET);
	oper.getIssueAssetBuilder().getAssetBuilder().getPropertyBuilder().setCode("coin");
	oper.getIssueAssetBuilder().getAssetBuilder().getPropertyBuilder().setType(Message.AssetProperty.Type.IOU);
	oper.getIssueAssetBuilder().getAssetBuilder().getPropertyBuilder().setIssuer(address);
	oper.getIssueAssetBuilder().getAssetBuilder().setAmount(1);

    // set signature list
	Message.Signature.Builder sign = Message.Signature.newBuilder();			
	BubiKey bubiKey = new BubiKey(privateKey);
	sign.setPublicKey(bubiKey.getB58PublicKey());
	sign.setSignData(ByteString.copyFrom(bubiKey.sign(tran.build().toByteArray())));
					
	Message.TransactionEnv.Builder env = Message.TransactionEnv.newBuilder(); 
	env.setTransaction(tran.build());
	env.addSignatures(sign);

    // send transaction
	chain_message_one_.Send(Message.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, env.build().toByteArray()));
} catch (Exception e1) {
	e1.printStackTrace();
}
````

##### 8.2.2 布比3.0版
```java
try {
	String privateKey = "c004e64b5b674eacf176d80cbbb9e427521cc228797d3351834a62cb8929c254ec7f60";
	String address = "a00415a62412ab9617b369b7c3832bb240b4f6a53b79f5";
	String httpRequest = "http://127.0.0.1:19333";
	String getAccount = url + "/getAccount?address=" + address;
	String txSeq = HttpKit.post(getAccount, "");
	JSONObject tx = JSONObject.parseObject(txSeq);
	String seq_str = tx.getJSONObject("result").containsKey("nonce") ? tx.getJSONObject("result").getString("nonce") : "0";
	long nonce = Long.parseLong(seq_str);
			
	// generate transaction
	Transaction.Builder tran = Transaction.newBuilder();
	tran.setSourceAddress(srcAddress);
	tran.setNonce(nonce + 1);
	
    // add operations
	Operation.Builder oper = tran.addOperationsBuilder();
	oper.setType(Operation.Type.ISSUE_ASSET);
	OperationIssueAsset.Builder issuer = OperationIssueAsset.newBuilder();
	issuer.setCode("coin");
	issuer.setAmount(1);
	oper.setIssueAsset(issuer);
	
    // add signature list
	Signature.Builder signature  = Signature.newBuilder();
	signature.setPublicKey(srcPublic);
	byte[] sign_data = BubiKey.sign(tran.build().toByteArray(), srcPrivate);
	signature.setSignData(ByteString.copyFrom(sign_data));
			
	TransactionEnv.Builder tranEnv = TransactionEnv.newBuilder(); 
	tranEnv.setTransaction(tran.build());
	tranEnv.addSignatures(signature.build());

	// send transaction
	chain_message_one_.Send(Overlay.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, tranEnv.build().toByteArray());
} catch (Exception e1) {
	e1.printStackTrace();
}
```

#### 8.3 转账
##### 8.3.1 布比2.0版
```java
try {
	String privateKey = "privbtZ1Fw5RRWD4ZFR6TAMWjN145zQJeJQxo3EXAABfgBjUdiLHLLHF";
	String address = "bubiV8i2558GmfnBREe87ZagdkKsfeJh5HYjcNpa";
	String destAddress = "bubiQ0i2558GmfnBREe87ZagdkKsfeJh5HHijops";
	String httpRequest = "http://127.0.0.1:19333";
	
    // get tx sequence number
	String url = httpRequest + "/getAccount?address=" + address;
	String txSeq = null;
	txSeq = HttpKit.post(url, "");
	JSONObject tx = JSONObject.parseObject(txSeq);
	String seq_str = tx.getJSONObject("result").getString("tx_seq");
	seq_ = Long.parseLong(seq_str);
	
    // generate transaction
	Message.Transaction.Builder tran = Message.Transaction.newBuilder();
	tran.setSourceAddress(address);
	tran.setFee(1000);
	tran.setSequenceNumber(seq_ + 1);
	tran.setMetadata(ByteString.copyFromUtf8(String.valueOf(System.currentTimeMillis())));
	
    // add operations
	Message.Operation.Builder oper = tran.addOperationsBuilder();
	oper.setType(Message.Operation.Type.PAYMENT);
    oper.getPaymentBuilder().setDestAddress(destAddress);
    oper.getPaymentBuilder().getAssetBuilder().getPropertyBuilder().setType(Message.AssetProperty.Type.IOU);
    oper.getPaymentBuilder().getAssetBuilder().getPropertyBuilder().setIssuer(address);
    oper.getPaymentBuilder().getAssetBuilder().getPropertyBuilder().setCode("coin");
    oper.getPaymentBuilder().getAssetBuilder().setAmount(1);
					
    // add signature list
	Message.Signature.Builder sign = Message.Signature.newBuilder();
	BubiKey bubiKey = new BubiKey(privateKey);
	sign.setPublicKey(bubiKey.getB58PublicKey());
	sign.setSignData(ByteString.copyFrom(bubiKey.sign(tran.build().toByteArray())));
					
	Message.TransactionEnv.Builder env = Message.TransactionEnv.newBuilder(); 
	env.setTransaction(tran.build());
	env.addSignatures(sign);
	
    // send transaction
	chain_message_one_.Send(Message.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, env.build().toByteArray());
} catch (Exception e1) {
	e1.printStackTrace();
}
```

##### 8.3.2 布比3.0版
```java
try {
	String privateKey = "c004e64b5b674eacf176d80cbbb9e427521cc228797d3351834a62cb8929c254ec7f60";
	String address = "a00415a62412ab9617b369b7c3832bb240b4f6a53b79f5";
	String destAddress = "a001f8a08bb1ce8085a22eaba4bbc3ba8a5f6e94033bc8";
	String httpRequest = "http://127.0.0.1:19333";
	
    // get tx sequence number
	String getAccount = url + "/getAccount?address=" + address;
	String txSeq = HttpKit.post(getAccount, "");
	JSONObject tx = JSONObject.parseObject(txSeq);
	String seq_str = tx.getJSONObject("result").containsKey("nonce") ? tx.getJSONObject("result").getString("nonce") : "0";
	long nonce = Long.parseLong(seq_str);
					
	// generate transaction
	Transaction.Builder tran = Transaction.newBuilder();
	tran.setSourceAddress(srcAddress);
	tran.setNonce(nonce + 1);
			
    // add operations
	Operation.Builder oper = tran.addOperationsBuilder();
	oper.setType(Operation.Type.PAYMENT);
	OperationPayment.Builder payment = OperationPayment.newBuilder();
	payment.setDestAddress(destAddress);
	Asset.Builder asset = Asset.newBuilder();
	asset.setAmount(1);
	AssetProperty.Builder assetProperty = AssetProperty.newBuilder();
	assetProperty.setCode("coin");
	assetProperty.setIssuer(address);
	asset.setProperty(assetProperty);
	
    // add signature list
	Signature.Builder signature  = Signature.newBuilder();
	signature.setPublicKey(srcPublic);
	byte[] sign_data = BubiKey.sign(tran.build().toByteArray(), srcPrivate);
	signature.setSignData(ByteString.copyFrom(sign_data));
					
	TransactionEnv.Builder tranEnv = TransactionEnv.newBuilder(); 
	tranEnv.setTransaction(tran.build());
	tranEnv.addSignatures(signature.build());

	// send transaction
	chain_message_one_.Send(Overlay.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, tranEnv.build().toByteArray());
} catch (Exception e1) {
	e1.printStackTrace();
}
```

### 9 节点间传递消息的例子
假如有两个应用A和B，一个应用A绑定了节点A(127.0.0.1:7053)，另一应用B绑定了节点B(127.0.0.1:7063)，节点A和节点B必须要同一链上

```java
String AddressA = "bubiV8i2558GmfnBREe87ZagdkKsfeJh5HYjcNpa"; // 节点A的地址
String AddressB = "bubiV8i2MLZd5ahDGay6oAZHiMyYNUkJfSiTAmJy" // 节点B的地址
private BlockChainAdapter chain_message_one_;
private BlockChainAdapter chain_message_two_;

chain_message_one_ = new BlockChainAdapter("ws://127.0.0.1:7053");
chain_message_two_ = new BlockChainAdapter("ws://127.0.0.1:7063");

// 应用A绑定CHAIN_PEER_MESSAGE_VALUE消息
chain_message_one_.AddChainMethod(Overlay.ChainMessageType.CHAIN_PEER_MESSAGE_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
        try {
        		Overlay.ChainPeerMessage chain_peer_message = Overlay.ChainPeerMessage.parseFrom(msg);
        		System.out.println(new String(chain_peer_message.getData().toByteArray()));
    	} catch (Exception e) {
	    }
	}
});

// 应用B绑定CHAIN_PEER_MESSAGE_VALUE消息
chain_message_two_.AddChainMethod(Overlay.ChainMessageType.CHAIN_PEER_MESSAGE_VALUE, new BlockChainAdapterProc() {
	public void ChainMethod (byte[] msg, int length) {
        try {
        		Overlay.ChainPeerMessage chain_peer_message = Overlay.ChainPeerMessage.parseFrom(msg);
        		System.out.println(new String(chain_peer_message.getData().toByteArray()));
    	} catch (Exception e) {
	    }
	}
});

// 布比2.0版
Message.ChainPeerMessage.Builder chain_peer_message = Message.ChainPeerMessage.newBuilder(); 
chain_peer_message.setSrcPeerAddr(AddressA); // 绑定节点A的地址，也可为null
chain_peer_message.addDesPeerAddrs(AddressB); // 绑定节点B的地址，不可为null
chain_peer_message.setData(ByteString.copyFromUtf8("test"));
if (!chain_message_one_.Send(Message.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, chain_peer_message.build().toByteArray())) {
	// 错误输出
}

// 布比3.0版
Overlay.ChainPeerMessage.Builder chain_peer_message3 = Overlay.ChainPeerMessage.newBuilder(); 
chain_peer_message3.setSrcPeerAddr(AddressA); // 绑定节点A的地址，也可为null
chain_peer_message3.addDesPeerAddrs(AddressB); // 绑定节点B的地址，不可为null
chain_peer_message3.setData(ByteString.copyFromUtf8("test"));
if (!chain_message_one_.Send(Overlay.ChainMessageType.CHAIN_SUBMITTRANSACTION_VALUE, chain_peer_message3.build().toByteArray())) {
	// 错误输出
}
```

发送完成后，应用B绑定的CHAIN_PEER_MESSAGE_VALUE消息的回调函数，就会打印出test