/*bubi 3.3版本开始用这个类生成私钥、公钥和地址*/

package cn.bubi.baas.utils.encryption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.sm2.SM2PrivateKey;
import cfca.sadk.algorithm.sm2.SM2PublicKey;
import cfca.sadk.algorithm.util.FileUtil;
import cfca.sadk.lib.crypto.JCrypto;
import cfca.sadk.lib.crypto.Session;
import cfca.sadk.org.bouncycastle.util.Arrays;
import cfca.sadk.util.Base64;
import cfca.sadk.util.CertUtil;
import cfca.sadk.util.HashUtil;
import cfca.sadk.util.KeyUtil;
import cfca.sadk.x509.certificate.X509Cert;
import cn.bubi.baas.utils.encryption.utils.Base58;
import cn.bubi.baas.utils.encryption.utils.BubiKeyMember;
import cn.bubi.baas.utils.encryption.utils.Common;
import cn.bubi.baas.utils.encryption.utils.HexFormat;
import cn.bubi.sm3.SM3Digest;
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;

public class BubiKey3 {
	private final static byte PUBLICKEY_PREFIX = (byte)0xB0;
	BubiKeyMember bubiKeyMember_ = new BubiKeyMember();
	
	public BubiKey3() throws Exception {
		this(BubiKeyType.ED25519);
	}
	
	/**
	 * 产生一个随机的私钥对象
	 * @param type
	 * @throws Exception 
	 */
	public BubiKey3(BubiKeyType type) throws Exception {
		bubiKeyMember_.setType_(type);
		switch (type) {
		case ED25519: {
			KeyPairGenerator keyPairGenerator = new KeyPairGenerator();
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			EdDSAPrivateKey priKey = (EdDSAPrivateKey) keyPair.getPrivate();
			EdDSAPublicKey pubKey = (EdDSAPublicKey) keyPair.getPublic();
			bubiKeyMember_.setRaw_skey_(priKey.getSeed());
			bubiKeyMember_.setRaw_pkey_(pubKey.getAbyte());
			break;
		}
		case ECCSM2: {
			final String deviceName = JCrypto.JSOFT_LIB;
	        JCrypto.getInstance().initialize(deviceName, null);
	        Session session = JCrypto.getInstance().openSession(deviceName);
	        
	        KeyPair keypair = KeyUtil.generateKeyPair(new Mechanism(Mechanism.SM2), 256, session);
	        SM2PublicKey pubKey = (SM2PublicKey)keypair.getPublic();
	        SM2PrivateKey priKey = (SM2PrivateKey)keypair.getPrivate();
	        bubiKeyMember_.setRaw_skey_(priKey.getD_Bytes());
	        bubiKeyMember_.setRaw_pkey_(Common.getSM2PublicKey(pubKey));
			break;
		}
		case RSA: {
			java.security.KeyPairGenerator keyPairGen = null;
			try {
				keyPairGen = java.security.KeyPairGenerator.getInstance("RSA");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			keyPairGen.initialize(1024);

			java.security.KeyPair keyPair = keyPairGen.generateKeyPair();
			RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
			RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
			bubiKeyMember_.setRaw_skey_(privateKey.getEncoded());
			bubiKeyMember_.setRaw_pkey_(publicKey.getEncoded());
			break;
		}
		default:
			throw new Exception(BubiKeyType.CFCA == type? "CFCA does not support this method" : "type does not exist");
		}
	}
	
	/**
	 * PFX、SM2 证书文件创建 CFCA 秘钥对；
	 * 
	 * @param fileType
	 * @param fileData
	 * @param filePwd
	 * @throws Exception
	 */
	public BubiKey3(CertFileType fileType, byte[] fileData, String filePwd) throws Exception {
		bubiKeyMember_.setType_(BubiKeyType.CFCA);
		switch(fileType) {
		case PFX: {
			RSAPrivateKey privateKey = (RSAPrivateKey) KeyUtil.getPrivateKeyFromPFX(fileData, filePwd);
			X509Cert rsaCert = CertUtil.getCertFromPFX(fileData, filePwd);
			bubiKeyMember_.setRaw_skey_(privateKey.getEncoded());
			bubiKeyMember_.setRaw_pkey_(Base64.encode(rsaCert.getEncoded()));
			break;
		}
		case SM2: {
			SM2PrivateKey privateKey = KeyUtil.getPrivateKeyFromSM2(fileData, filePwd);
			X509Cert sm2Cert = CertUtil.getCertFromSM2(fileData);
			bubiKeyMember_.setRaw_skey_(privateKey.getD_Bytes());
			bubiKeyMember_.setRaw_pkey_(Base64.encode(sm2Cert.getEncoded()));
			break;
		}
		default:
			throw new Exception("wrong file type");
		}
	}
	
	/**
	 * PFX、SM2 证书文件创建 CFCA 秘钥对；
	 * 
	 * @param fileType
	 * @param filePath
	 * @param filePwd
	 * @throws Exception
	 */
	public BubiKey3(CertFileType fileType, String filePath, String filePwd) throws Exception {
		bubiKeyMember_.setType_(BubiKeyType.CFCA);
		switch(fileType) {
		case PFX: {
			RSAPrivateKey privateKey = (RSAPrivateKey) KeyUtil.getPrivateKeyFromPFX(filePath, filePwd);
			X509Cert rsaCert = CertUtil.getCertFromPFX(filePath, filePwd);
			bubiKeyMember_.setRaw_skey_(privateKey.getEncoded());
			bubiKeyMember_.setRaw_pkey_(Base64.encode(rsaCert.getEncoded()));
			break;
		}
		case SM2: {
			SM2PrivateKey privateKey = KeyUtil.getPrivateKeyFromSM2(filePath, filePwd);
			X509Cert sm2Cert = CertUtil.getCertFromSM2(filePath);
			bubiKeyMember_.setRaw_skey_(privateKey.getD_Bytes());
			bubiKeyMember_.setRaw_pkey_(Base64.encode(sm2Cert.getEncoded()));
			break;
		}
		default:
			throw new Exception("wrong file type");
		}
	}
	
	/**
	 * JKS证书文件创建 CFCA 秘钥对；
	 * 
	 * @param jksfileData
	 * @param jksfilePwd
	 * @param alias
	 * @throws Exception
	 */
	public BubiKey3(byte[] jksfileData, String jksfilePwd, String alias) throws Exception {
		bubiKeyMember_.setType_(BubiKeyType.CFCA);
		String tempFileDir = System.getProperty("java.io.tmpdir") + "bubikey-" + UUID.randomUUID().toString() + ".jks";
		File file = new File(tempFileDir);
		OutputStream outStream = new FileOutputStream(file);
		FileUtil.writeBytesToFile(jksfileData, outStream);
		outStream.close();
		RSAPrivateKey privateKey = (RSAPrivateKey) KeyUtil.getPrivateKeyFromJKS(tempFileDir, jksfilePwd, alias);
		X509Cert x509Cert = CertUtil.getCertFromJKS(tempFileDir, jksfilePwd, alias);
		file.delete();
		bubiKeyMember_.setRaw_skey_(privateKey.getEncoded());
		bubiKeyMember_.setRaw_pkey_(Base64.encode(x509Cert.getEncoded()));
	}
	
	/**
	 * JKS证书文件创建 CFCA 秘钥对；
	 * 
	 * @param jksfileData
	 * @param jksfilePwd
	 * @param alias
	 * @throws Exception
	 */
	public BubiKey3(String jksfilePath, String jksfilePwd, String alias) throws Exception {
		bubiKeyMember_.setType_(BubiKeyType.CFCA);
		RSAPrivateKey privateKey = (RSAPrivateKey) KeyUtil.getPrivateKeyFromJKS(jksfilePath, jksfilePwd, alias);
		X509Cert x509Cert = CertUtil.getCertFromJKS(jksfilePath, jksfilePwd, alias);
		bubiKeyMember_.setRaw_skey_(privateKey.getEncoded());
		bubiKeyMember_.setRaw_pkey_(Base64.encode(x509Cert.getEncoded()));
	}
	
	/**
	 * 通过一份 CFCA 签名摘要解析其公钥；
	 * 
	 * @param p7SignedData
	 */
	public BubiKey3(byte[] p7SignedData) {
		try {
			bubiKeyMember_.setType_(BubiKeyType.CFCA);
			final cfca.sadk.util.Signature signature = new cfca.sadk.util.Signature();
			X509Cert x509Cert = signature.getSignerX509CertFromP7SignData(p7SignedData);
			//bubiKeyMember.setRaw_pkey_(x509Cert.getPublicKeyData();
			bubiKeyMember_.setRaw_pkey_(Base64.encode(x509Cert.getEncoded()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 传私钥构造，除CFCA外，其他公钥可为空；
	 * @param bSkey 私钥字符串
	 * @param bPkey 公钥字符串
	 * @throws Exception
	 */
	public BubiKey3(String bSkey) throws Exception {
		this(bSkey, null);
	}
	
	/**
	 * 传私钥和公钥构造，除CFCA外，其他公钥可为空；
	 * @param bSkey 私钥字符串
	 * @param bPkey 公钥字符串
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 */
	public BubiKey3(String bSkey, String bPkey) throws Exception {
		BubiKeyMember keyMember = new BubiKeyMember();
		getPrivateKey(bSkey, keyMember);
		
		boolean isPkeyEmpty = true;
		if (bPkey != null && !bPkey.isEmpty()) {
			getPublicKey(bPkey, keyMember);
			isPkeyEmpty = false;
		}
		
		getPublicKey(isPkeyEmpty, keyMember);
		
		if (keyMember.getRaw_skey_() != null && keyMember.getRaw_pkey_()!= null) {
			bubiKeyMember_.setType_(keyMember.getType_());
			bubiKeyMember_.setRaw_skey_(keyMember.getRaw_skey_());
			bubiKeyMember_.setRaw_pkey_(keyMember.getRaw_pkey_());
		}
		else {
			throw new Exception((keyMember.getRaw_skey_() == null ? "private key is invalid" : "public key is invalid") + "please check");
		}
	}
	
	/**
	 * 对消息进行签名
	 * @param msg 要签名的消息
	 * @return 签名内容
	 * @throws Exception
	 */
	public byte[] sign(byte[] msg) throws Exception {
		return sign(msg, bubiKeyMember_);
	}
	
	/**
	 * 对消息进行签名, CFCA不支持
	 * @param msg 要签名的消息
	 * @param bSkey 私钥
	 * @return 签名内容
	 * @throws Exception
	 */
	public static byte[] sign(byte[] msg, String bSkey) throws Exception {
		return sign(msg, bSkey, null);
	}
	
	/**
	 * 对消息进行签名，支持CFCA
	 * @param msg 要签名的消息
	 * @param bSkey 私钥
	 * @param bPkey 公钥
	 * @return 签名内容
	 * @throws Exception
	 */
	public static byte[] sign(byte[] msg, String bSkey, String bPkey) throws Exception {
		BubiKeyMember keyMember = new BubiKeyMember();
		getPrivateKey(bSkey, keyMember);
		boolean isPkeyEmpty = true;
		if (bPkey != null && !bPkey.isEmpty()) {
			getPublicKey(bPkey, keyMember);
			isPkeyEmpty = false;
		}
		getPublicKey(isPkeyEmpty, keyMember);
		
		return sign(msg, keyMember);
	}
	
	/**
	 * 验证签名函数
	 * @param msg 消息内容
	 * @param pkey 公钥字符串
	 * @param sig 签名
	 * @return
	 * @throws Exception
	 */
	public boolean verify(byte[] msg, byte[] signMsg) throws Exception {
		boolean verifySuccess = false;
		verifySuccess = verify(msg, signMsg, bubiKeyMember_);
		
		return verifySuccess;
	}
	
	/**
	 * 验证签名函数
	 * @param msg 消息内容
	 * @param pkey 公钥字符串
	 * @param sig 签名
	 * @return
	 * @throws Exception 
	 */
	public static boolean verify(byte[] msg, byte[] signMsg, String bPkey) throws Exception {
		boolean verifySuccess = false;
		BubiKeyMember member = new BubiKeyMember();
		getPublicKey(bPkey, member);
		verifySuccess = verify(msg, signMsg, member);
		
		return verifySuccess;
	}
	
	/**
	 *
	 * @return 编码后的私钥
	 * @throws Exception 
	 */
	public String getEncPrivateKey() throws Exception {
		return b58PrivateKey(bubiKeyMember_.getType_(), bubiKeyMember_.getRaw_skey_());
	}
	
	/**
	 *  
	 * @return 编码后的公钥
	 * @throws Exception 
	 */
	public String getEncPublicKey() throws Exception {
		return b16PublicKey(bubiKeyMember_.getType_(), bubiKeyMember_.getRaw_pkey_()).toLowerCase();
	}
	
	/**
	 * 根据私钥计算公钥，CFCA不支持
	 * @param bSkey 私钥字符串
	 * @return 布比3.3公钥
	 * @throws Exception 
	 */
	public static String getEncPublicKey(String bSkey) throws Exception {
		BubiKeyMember member = new BubiKeyMember();
		getPrivateKey(bSkey, member);
		getPublicKey(true, member);
		return b16PublicKey(member.getType_(), member.getRaw_pkey_()).toLowerCase();
	}
	
	/**
	 * @return 返回bubi地址
	 * @throws Exception 
	 */
	public String getBubiAddress() throws Exception {
		byte[] raw_pkey = bubiKeyMember_.getRaw_pkey_();
		if (null == raw_pkey) {
			throw new Exception("public key is null");
		}
		
		return b58Address(bubiKeyMember_.getType_(), raw_pkey);
	}
	
	/**
	 * 根据公钥计算bubi地址
	 * @param pKey 公钥字符串
	 * @return bubi2.0地址
	 * @throws Exception 
	 */
	public static String getBubiAddress(String pKey) throws Exception {
		BubiKeyMember member = new BubiKeyMember();
		getPublicKey(pKey, member);
		
		return b58Address(member.getType_(), member.getRaw_pkey_());
	}
	
	private static String b58PrivateKey(BubiKeyType type, byte[] raw_skey) throws Exception {
		if (null == raw_skey) {
			throw new Exception("private key is null");
		}
		byte[] buff = new byte[raw_skey.length + 5];
		buff[0] = (byte) 0xDA;
		buff[1] = (byte) 0x37;
		buff[2] = (byte) 0x9F;
		System.arraycopy(raw_skey, 0, buff, 4, raw_skey.length);
		
		buff[3] = (byte) (type.ordinal() + 1);
		
		byte[] hash1 = CalHash(type, buff);
		byte[] hash2 = CalHash(type, hash1);

		byte[] tmp = new byte[buff.length + 4];

		System.arraycopy(buff, 0, tmp, 0, buff.length);
		System.arraycopy(hash2, 0, tmp, buff.length, 4);
		
		return Base58.encode(tmp);
	}
	
	private static String b16PublicKey(BubiKeyType type, byte[] raw_pkey) throws Exception {
		if (null == raw_pkey) {
			throw new Exception("public key is null");
		}
		int length = raw_pkey.length + 2;
		byte[] buff = new byte[length];
		buff[0] = PUBLICKEY_PREFIX;
		buff[1] = (byte) (type.ordinal() + 1);

		System.arraycopy(raw_pkey, 0, buff, 2, raw_pkey.length);
		
		byte[] hash1 = CalHash(type, buff);
		byte[] hash2 = CalHash(type, hash1);
		byte[] tmp = new byte[buff.length + 4];

		System.arraycopy(buff, 0, tmp, 0, buff.length);
		System.arraycopy(hash2, 0, tmp, buff.length, 4);
		
		return HexFormat.byteToHex(tmp);
	}
	
	private static String b58Address(BubiKeyType type, byte[] raw_pkey) {
		byte[] buff = new byte[23];
		buff[0] = (byte) 0x01;
		buff[1] = (byte) 0x56;
		buff[2] = (byte) (type.ordinal() + 1);

		byte[] hashPkey = CalHash(type, raw_pkey);
		System.arraycopy(hashPkey, 12, buff, 3, 20);
		
		byte[] hash1 = CalHash(type, buff);
		byte[] hash2 = CalHash(type, hash1);
		byte[] tmp = new byte[27];
		
		System.arraycopy(buff, 0, tmp, 0, buff.length);
		System.arraycopy(hash2, 0, tmp, buff.length, 4);
		
		return Base58.encode(tmp);
	}
	
	private static byte[] sign(byte[] msg, BubiKeyMember keyMember) throws Exception {
		byte[] rawSkey = keyMember.getRaw_skey_();
		if (null == rawSkey) {
			throw new Exception("private key is null");
		}
		
		byte[] rawPkey = keyMember.getRaw_pkey_();
		byte[] signMessage = null;
		
		switch (keyMember.getType_()) {
		case ED25519: {
			Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
			EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");
			EdDSAPrivateKeySpec sKeySpec = new EdDSAPrivateKeySpec(rawSkey, spec);
			PrivateKey sKey = new EdDSAPrivateKey(sKeySpec);
			sgr.initSign(sKey);
			sgr.update(msg);
			
			signMessage = sgr.sign();
			break;
		}
			
			
		case ECCSM2: {
			final String deviceName = JCrypto.JSOFT_LIB;
			JCrypto.getInstance().initialize(deviceName, null);
			Session session = JCrypto.getInstance().openSession(deviceName);
			final cfca.sadk.util.Signature signature = new cfca.sadk.util.Signature();
			
			SM2PrivateKey privateKey = KeyUtil.getSM2PrivateKey(rawSkey, null, null) ;
			SM2PublicKey publicKey  = Common.getSM2PublicKey(rawPkey);
			final byte[] userId = "1234567812345678".getBytes("UTF8");
			final String signAlg = Mechanism.SM3_SM2;
	        // 
	        byte[] hash = HashUtil.SM2HashMessageByBCWithZValue(userId, msg, publicKey.getPubXByInt(), publicKey.getPubYByInt());
	        signMessage = Common.ASN1toRS(Base64.decode(signature.p1SignByHash(signAlg, hash, privateKey, session)));
			break;
		}
			
		case RSA: {
			PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(rawSkey);

			KeyFactory keyf = KeyFactory.getInstance("RSA");
			PrivateKey priKey = keyf.generatePrivate(priPKCS8);

			Signature signature = Signature.getInstance("SHA1WithRSA");

			signature.initSign(priKey);
			signature.update(msg);

			signMessage = signature.sign();
			break;
		}
		case CFCA: {
			final String deviceName = JCrypto.JSOFT_LIB;
			JCrypto.getInstance().initialize(deviceName, null);
			Session session = JCrypto.getInstance().openSession(deviceName);
			final cfca.sadk.util.Signature signature = new cfca.sadk.util.Signature();
			
			PrivateKey privateKey = null;
			String signAlg = null;
			byte[] hash = null;
			
			X509Cert x509Cert = new X509Cert(rawPkey);
			if (CertUtil.isSM2Cert(x509Cert)) {
				privateKey = KeyUtil.getSM2PrivateKey(rawSkey, null, null) ;
				SM2PublicKey sm2PublicKey = (SM2PublicKey) x509Cert.getPublicKey();
				
				final byte[] userId = "1234567812345678".getBytes("UTF8");
				hash = HashUtil.SM2HashMessageByBCWithZValue(userId, msg, 
						sm2PublicKey.getPubXByInt(), sm2PublicKey.getPubYByInt());// SM2签名包含Z值
				
				signAlg = Mechanism.SM3_SM2;
			}
			else {
				PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(rawSkey);
				
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privatePKCS8);
				
				hash = HashUtil.RSAHashMessageByBC(msg, new Mechanism(Mechanism.SHA256), false);
				
				signAlg = Mechanism.SHA256_RSA;
			}
			
			signMessage = signature.p7SignByHash(signAlg, hash, privateKey, x509Cert, session);
			break;
		}
		default:
			throw new Exception("type does not exist");
		}
		
		return signMessage;
	}
	
	private static boolean verify(byte[] msg, byte[] sign, BubiKeyMember member) throws Exception {
		boolean verifySuccess = false;
		byte[] rawpkey = member.getRaw_pkey_();
		switch (member.getType_()) {
		case ED25519: {
			Signature sgr = new EdDSAEngine(MessageDigest.getInstance("SHA-512"));
			EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");
			EdDSAPublicKeySpec pubKey = new EdDSAPublicKeySpec(rawpkey, spec);
			PublicKey vKey = new EdDSAPublicKey(pubKey);
			sgr.initVerify(vKey);
			sgr.update(msg);
			verifySuccess = sgr.verify(sign);
			break;
		}
		case ECCSM2: { // SM2
			final String deviceName = JCrypto.JSOFT_LIB;
			JCrypto.getInstance().initialize(deviceName, null);
			Session session = JCrypto.getInstance().openSession(deviceName);
			final cfca.sadk.util.Signature signature = new cfca.sadk.util.Signature();
			
			SM2PublicKey publicKey  = Common.getSM2PublicKey(rawpkey);
			final byte[] userId = "1234567812345678".getBytes("UTF8");
			final String signAlg = Mechanism.SM3_SM2;
	        // 
	        byte[] hash = HashUtil.SM2HashMessageByBCWithZValue(userId, msg, publicKey.getPubXByInt(), publicKey.getPubYByInt());
	        verifySuccess = signature.p1VerifyByHash(signAlg, hash, Base64.encode(sign), publicKey, session);
			break;
		}
		case RSA: { // RSA
			KeySpec keySpec = new X509EncodedKeySpec(rawpkey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey pubKey = keyFactory.generatePublic(keySpec);

			java.security.Signature signature = java.security.Signature.getInstance("SHA1WithRSA");

			signature.initVerify(pubKey);
			signature.update(msg);

			verifySuccess = signature.verify(sign);
			break;
		}
		case CFCA: { // CFCA
			final String deviceName = JCrypto.JSOFT_LIB;
			JCrypto.getInstance().initialize(deviceName, null);
			Session session = JCrypto.getInstance().openSession(deviceName);
			final cfca.sadk.util.Signature signature = new cfca.sadk.util.Signature();
			
			byte[] hash = null;
			
			X509Cert x509Cert = new X509Cert(rawpkey);
			if (CertUtil.isSM2Cert(x509Cert)) {
				SM2PublicKey publicKey = (SM2PublicKey)x509Cert.getPublicKey() ;

				final byte[] userId = "1234567812345678".getBytes("UTF8");
				hash = HashUtil.SM2HashMessageByBCWithZValue(userId, msg, 
						publicKey.getPubXByInt(), publicKey.getPubYByInt());// SM2签名包含Z值
			}
			else {
				hash = HashUtil.RSAHashMessageByBC(msg, new Mechanism(Mechanism.SHA256), false);
			}
			
			verifySuccess = signature.p7VerifyByHash(hash, sign, session);
			break;
		}
		}
		return verifySuccess;
	}
	
	private static void getPrivateKey(String bSkey, BubiKeyMember member) throws Exception {
		if (null == bSkey) {
			throw new Exception("private key cannot be null");
		}
		
		byte[] skey = Base58.decode(bSkey);
		if (skey.length <= 9) {
			throw new Exception("private key (" + bSkey + ") is invalid");
		}
		
		// 3字节前缀，1字节类型，n字节私钥，4字节校验码
		if (skey[3] > 4 || skey[3] < 1) {
			throw new Exception("private key (" + bSkey + ") is invalid");
		}
		BubiKeyType type = BubiKeyType.values()[skey[3] - 1];
		
		// 验证checksum
		if (!CheckSum(type, skey)) {
			throw new Exception("private key (" + bSkey + ") is invalid");
		}
		
		byte[] rawSKey = new byte[skey.length - 9];
		System.arraycopy(skey, 4, rawSKey, 0, rawSKey.length);
		
		member.setType_(type);
		member.setRaw_skey_(rawSKey);
	}
	
	private static void getPublicKey(String bPkey, BubiKeyMember member) throws Exception {
		if (null == bPkey) {
			throw new Exception("public key cannot be null");
		}
		
		BubiKeyType type = null;
		byte[] buffPKey = HexFormat.hexToByte(bPkey);
		
		if (buffPKey.length < 6) {
			throw new Exception("public key (" + bPkey + ") is invalid, please check");
		}
		
		if (buffPKey[0] != PUBLICKEY_PREFIX) {
			throw new Exception("public key (" + bPkey + ") is invalid, please check");
		}
		
		// 1字节前缀，1字节类型，n字节私钥，4字节校验码
		if (buffPKey[1] > 4 || buffPKey[1] < 1) {
			throw new Exception("public key (" + bPkey + ") is invalid, please check");
		}
		type = BubiKeyType.values()[buffPKey[1] - 1];
		
		// 验证checksum
		if (!CheckSum(type, buffPKey)) {
			throw new Exception("public key (" + bPkey + ") is invalid, please check");
		}
		
		byte[] rawPKey = new byte[buffPKey.length - 6];
		System.arraycopy(buffPKey, 2, rawPKey, 0, rawPKey.length);
		
		member.setRaw_pkey_(rawPKey);
		member.setType_(type);
	}
	
	private static void getPublicKey(boolean isPkeyEmpty, BubiKeyMember member) throws Exception {
		byte[] rawSkey = member.getRaw_skey_();
		byte[] rawPkey = member.getRaw_pkey_();
		
		switch (member.getType_()) {
		case ED25519: {
	        EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");
	        EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(rawSkey, spec);
	        EdDSAPublicKeySpec spec2 = new EdDSAPublicKeySpec(privKey.getA(),spec);
	        EdDSAPublicKey pDsaPublicKey = new EdDSAPublicKey(spec2);
	        if (isPkeyEmpty) {
	        	member.setRaw_pkey_(pDsaPublicKey.getAbyte());
	        }
	        else if (!Arrays.areEqual(pDsaPublicKey.getAbyte(), rawPkey)) {
	        	throw new Exception("the private key does not match the public key, please check");
	        }
			break;
		}
		case ECCSM2: {
			SM2PrivateKey privateKey = new SM2PrivateKey(rawSkey);
			SM2PublicKey publicKey =  privateKey.getSM2PublicKey();
			if (isPkeyEmpty) {
				member.setRaw_pkey_(Common.getSM2PublicKey(publicKey));
	        }
			else if (!Arrays.areEqual(Common.getSM2PublicKey(publicKey), rawPkey)) {
				throw new Exception("the private key does not match the public key, please check");
			}
			break;
		}
		case RSA: {
			// java.security.KeyPair
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(rawSkey);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey myPrivateKey = keyFactory.generatePrivate(keySpec);
			RSAPrivateCrtKey privk = (RSAPrivateCrtKey) myPrivateKey;
			RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privk.getModulus(),
					privk.getPublicExponent());
			PublicKey myPublicKey = keyFactory.generatePublic(publicKeySpec);
			if (isPkeyEmpty) {
				member.setRaw_pkey_(myPublicKey.getEncoded());
	        }
			else if (!Arrays.areEqual(myPublicKey.getEncoded(), rawPkey)){
				throw new Exception("the private key does not match the public key, please check");
			}
			break;
		}
		case CFCA: {
			if (isPkeyEmpty) {
				throw new Exception("the public key can not be null when CFCA");
			}
			final String deviceName = JCrypto.JSOFT_LIB;
			JCrypto.getInstance().initialize(deviceName, null);
			PrivateKey privateKey = null;
			
			X509Cert x509Cert = new X509Cert(rawPkey);
			if (CertUtil.isSM2Cert(x509Cert)) {
				privateKey = new SM2PrivateKey(rawSkey) ;
				SM2PublicKey sm2PublicKey = (SM2PublicKey) x509Cert.getPublicKey();
				if (!Arrays.areEqual(((SM2PrivateKey)privateKey).getSM2PublicKey().getEncoded(), sm2PublicKey.getEncoded())) {
		        	throw new Exception("the private key does not match the public key, please check");
		        }
			}
			else {
				PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(rawSkey);
				
				KeyFactory keyFactory = KeyFactory.getInstance("RSA");
				privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privatePKCS8);
				
				RSAPrivateCrtKey privk = (RSAPrivateCrtKey)privateKey;
				RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privk.getModulus(),
						privk.getPublicExponent());
				PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
				
				byte[] pubKey = publicKey.getEncoded();
				byte[] certPkey = x509Cert.getPublicKey().getEncoded();
				if (!Arrays.areEqual(pubKey, certPkey)) {
		        	throw new Exception("the private key does not match the public key, please check");
		        }
			}
			break;
		}
		}
	}
	
	private static byte[] CalHash(BubiKeyType type, byte[] data) {
		byte[] result = null;
		if (type == BubiKeyType.ED25519) {
			MessageDigest sha256 = null;
			try {
				sha256 = MessageDigest.getInstance("SHA-256");

			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			sha256.update(data);
			result = sha256.digest();
		}
		else {
			result = SM3Digest.Hash(data);
		}
		return result;
	}
	
	private static boolean CheckSum(BubiKeyType type, byte[] key) {
		boolean SumIsRight = true;
		byte[] checkSrc = new byte[key.length - 4];
		byte[] checkSum = new byte[4];
		System.arraycopy(key, 0, checkSrc, 0, checkSrc.length);
		System.arraycopy(key, checkSrc.length, checkSum, 0, 4);
		
		byte[] hash1 = CalHash(type, checkSrc);
		byte[] hash2 = CalHash(type, hash1);
		
		byte[] HashSum = new byte[4];
		System.arraycopy(hash2, 0, HashSum, 0, 4);
		if (!Arrays.areEqual(HashSum, checkSum)) {
			SumIsRight = false;
		}
		return SumIsRight;
	}
}
