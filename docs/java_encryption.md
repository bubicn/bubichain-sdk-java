# __布比JAVA ENCRYPTIOIN使用文档__

## 1 用途
用于生成公私钥和地址，以及签名，和验签，BubiKey主要针对布比3.3以下的版本，BubiKey3主要针对布比3.3及以上的版本

## 2 jar包引用
所依赖的jar包在jar文件夹中寻找，依赖的jar包如下：

1. utils-encryption-1.3.16-SNAPSHOT.jar:BubiKey的相关操作和BubkKey3的相关操作，详细请看下面介绍
2. sadk-3.2.3.0.RELEASE.jar:用于CFCA的签名操作
3. eddsa-0.1.0.jar：ed25519签名包
4. bcprov-jdk15on-1.52.jar：证书操作依赖包

## 2 构造BubiKey对象
### 2.1 签名方式构造
该方式的参数只有一个，就是签名方式。

只有ED25519，ECCSM2，RSA支持方式，CFCA不支持。

示例：
```java
BubiKey bubiKey = new BubiKey(BubiKeyType.ED25519);
````

### 2.2 公钥构造
该方式的参数有一个，就是私钥。

只有ED25519，ECCSM2，RSA支持方式，CFCA不支持。

示例如下：
```java
String privateKey;
String publicKey;
BubiKey bubiKey = new BubiKey(privateKey);
```

### 2.3 公私钥构造
该方式的参数有两个，第一个是编码后的私钥，第二个参数是编码后的公钥

注意：
1、公钥不为空时，所有的签名都支持
2、公钥为空时，只有ED25519，ECCSM2，RSA支持方式，CFCA不支持

示例如下：
```java
String privateKey;
String publicKey;
BubiKey bubiKey = new BubiKey(privateKey, publicKey);
或
BubiKey bubiKey = new BubiKey(privateKey, null);
```

### 2.4 签名信息构造
该方式的参数只有一个，就是签名信息。
只有CFCA支持

示例如下：
```java
byte[] sign;
BubiKey bubiKey = new BubiKey(sign);
```

### 2.5 证书构造

#### 2.5.1 PFX和SM2
该方式的参数有三个，第一个是证书类型(PFX或SM2)，第二个是证书路径或证书内容(BASE64 编码格式或者 DER 编码格式数据)，第三个是证书访问口令。

示例如下：
```java
String filePath;
byte[] fileData;
String password;
BubiKey bubiKey = new BubiKey(CertFileType.SM2, filePath, password);
或
BubiKey bubiKey = new BubiKey(CertFileType.SM2, fileData, password);
````

#### 2.5.2 JKS
该方式的参数有三个，第一个是证书内容或证书路径，第二个是证书访问口令，第三个是别名。

示例如下：
```java
String filePath;
byte[] fileData;
String password;
String alias;
BubiKey bubiKey = new BubiKey(filePath, password, alias);
或
BubiKey bubiKey = new BubiKey(fileData, password, alias);
```

### 3 接口详细描述
#### 3.1签名（非静态）
方法名: sign
注意：调用此方法需要构造BubiKey对象

请求参数：

|变量|类型|描述
|:--- | --- | --- 
| msg | byte[] | 待签名信息

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| signMsg | byte[] | 签名后信息

例如：
```java
BubiKey bubiKey = new BubiKey(BubiKeyType.ED25519);
或
BubiKey3 bubiKey = new BubiKey3(BubiKeyType.ED25519);
String src = "test";
byte[] signMsg = bubiKey.sign(src.getBytes());
```

#### 3.2 签名（静态）
方法名: sign
注意：调用此方法不需要构造BubiKey对象

请求参数：

|变量|类型|描述
|:--- | --- | --- 
| msg | byte[] | 待签名信息
| privateKey | String | 私钥
| publicKey | String | 公钥（可为null,此时CFCA不支持）

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| signMsg | byte[] | 签名后信息

例如：
```java
String src = "test";
String privateKey;
String publicKey;
byte[] sign = BubiKey.sign(src.getBytes(), privateKey, publicKey);
或
byte[] sign = BubiKey3.sign(src.getBytes(), privateKey, publicKey);
```

#### 3.3 验签（非静态）
方法名: verify
注意：调用此方法需要构造BubiKey对象

请求参数：

|变量|类型|描述
|:--- | --- | --- 
| msg | byte[] | 签名原信息
| signMsg | byte[] | 签名后信息

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| verify | boolean | 验签结果

例如：
```java
String src = "test";
BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
或
BubiKey3 bubiKey = new BubiKey3(BubiKeyType.ECCSM2);
byte[] sign = bubiKey.sign(src.getBytes());
Boolean verifyResult = bubiKey.verify(src.getBytes(), sign);
```

##### 3.4 验签（静态）
方法名: verify
注意：调用此方法不需要构造BubiKey对象

请求参数：

|变量|类型|描述
|:--- | --- | --- 
| msg | byte[] | 签名原信息
| signMsg | byte[] | 签名后信息
| publicKey | String | 公钥

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| verify | boolean | 验签结果

例如：
```java
String src = "test";
String privateKey;
String publicKey;
byte[] sign = BubiKey.sign(src.getBytes(), privateKey, publicKey);
Boolean verifyResult = BubiKey.verify(src.getBytes(), sign, KeyFormatType.B58, publicKey);
或
byte[] sign = BubiKey3.sign(src.getBytes(), privateKey, publicKey);
Boolean verifyResult = BubiKey3.verify(src.getBytes(), sign, KeyFormatType.B58, publicKey);
```


#### 3.5 获取B58私钥（非静态）
方法名：getB58PrivKey
注意：调用此方法需要构造BubiKey对象，用于bubi 2.0程序

请求参数：无

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| b58PrivateKey | String | Base58编码后的私钥

例如：
```java
BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
String privateKey = bubiKey.getB58PrivKey();
```

#### 3.6 获取B58公钥（非静态）
方法名：getB58PublicKey
注意：调用此方法需要构造BubiKey对象，用于bubi 2.0程序

请求参数：无

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| b58PublicKey | String | Base58编码后的公钥

例如：
```java
BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
String publicKey = bubiKey.getB58PublicKey();
```

#### 3.7 获取B58公钥（静态）
方法名：getB58PublicKey
注意：调用此方法不需要构造BubiKey对象，用于bubi 2.0程序

请求参数：

|变量|类型|描述
|:--- | --- | --- 
| b58PrivateKey或b16PrivateKey | String | Base58编码的私钥或16进制编码的私钥

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| b58PublicKey | String | Base58编码后的公钥

例如：
```java
String b58privateKey;
String publicKey = BubiKey.getB58PublicKey(b58privateKey);
```

#### 3.8 获取B58地址（非静态）
方法名：getB58Address
注意：调用此方法需要构造BubiKey对象，用于bubi 2.0程序

请求参数： 无

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| b58address | String | Base58编码后的地址

例如：
```java
BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
String address = bubiKey.getB58Address();
```

#### 3.9 获取B58地址（静态）
方法名：getB58Address
注意：调用此方法不需要构造BubiKey对象，用于bubi 2.0程序

请求参数：

|变量|类型|描述
|:--- | --- | --- 
| b58PrivateKey或b16PrivateKey | String | Base58编码的私钥或16进制编码的私钥

返回结果：

|变量|类型|描述
|:--- | --- | --- 
| b58Address | String | Base58编码后的地址

例如：
```java
String publicKey;
String address = bubiKey.getB58Address(publicKey);
```

#### 3.10 获取B16公私钥地址
获取B16私钥、公钥、地址与B58一样，只需要将所有的B58改为B16即可，B16的私钥、公钥、地址是用于bubi 3.0。

例如
```java
BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
String privateKey = bubiKey.getB16PrivKey();
String publicKey = bubiKey.getB16PublicKey();
String publicKeyStatic = BubiKey.getB16PublicKey(privateKey);
String address = bubiKey.getB16Address();
String addressStatic = bubiKey.getB16Address(publicKey);
```

#### 3.11 获取布比3.3版本以上的公私钥地址
构造、签名和验签的接口名是一样的，获取私钥、公钥和地址的接口名发生变化
getEncPrivateKey、getEncPublicKey()、getBubiAddress()

注意：主要用于布比3.3及以上版本

例如：
```java
BubiKey bubiKey = new BubiKey(BubiKeyType.ECCSM2);
String privateKey = bubiKey.getEncPrivateKey();
String publicKey = bubiKey.getEncPublicKey();
String publicKeyStatic = BubiKey.getEncPublicKey(privateKey);
String address = bubiKey.getBubiAddress();
String addressStatic = bubiKey.getBubiAddress(publicKey);
```

#### 3.12 计算hash
方法名：GenerateHashHex
路径：cn.bubi.baas.utils.encryption.utils.HashUtil

请求参数：

|变量|类型|描述
|:--- | --- | --- 
| src | byte[] | 待计算的字节数组，在布比中是交易的序列化字节数组
| type | Integer | hash类型,0是sha-256,1是sm3

例如
```java
String url="127.0.0.1:19333";
String getHello = url + "/hello";
String hello = HttpKit.post(getHello, "");
JSONObject ho = JSONObject.parseObject(hello);
Integer hash_type = ho.containsKey("hash_type") ? ho.getInteger("hash_type") : 0;
Transaction.Builder tran = Transaction.newBuilder();
String hash = HashUtil.GenerateHashHex(tran.build().toByteArray(), hash_type);
```

若要获取bubi底层的hash类型，需要访问http的hello接口，会返回hash类型