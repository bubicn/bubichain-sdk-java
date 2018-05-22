package cn.bubi.encryption;

import cfca.sadk.algorithm.common.Mechanism;
import cfca.sadk.algorithm.sm2.SM2PrivateKey;
import cfca.sadk.algorithm.sm2.SM2PublicKey;
import cfca.sadk.algorithm.util.BigIntegerUtil;
import cfca.sadk.algorithm.util.FileUtil;
import cfca.sadk.lib.crypto.JCrypto;
import cfca.sadk.lib.crypto.Session;
import cfca.sadk.org.bouncycastle.asn1.ASN1Integer;
import cfca.sadk.org.bouncycastle.asn1.ASN1Sequence;
import cfca.sadk.org.bouncycastle.util.Arrays;
import cfca.sadk.util.Base64;
import cfca.sadk.util.CertUtil;
import cfca.sadk.util.HashUtil;
import cfca.sadk.util.KeyUtil;
import cfca.sadk.x509.certificate.X509Cert;
import cn.bubi.crc8.CRC8;
import cn.bubi.encryption.utils.Base58;
import cn.bubi.encryption.utils.BubiKeyMember;
import cn.bubi.encryption.utils.HexFormat;
import cn.bubi.sm3.SM3Digest;
import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;
import net.i2p.crypto.eddsa.spec.EdDSAPrivateKeySpec;
import net.i2p.crypto.eddsa.spec.EdDSAPublicKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.*;
import java.util.UUID;

public class BubiKey{
    private final static byte ADDRESS_PREFIX = (byte) 0xA0;
    private final static byte PUBLICKEY_PREFIX = (byte) 0xB0;
    private final static byte PRIVATEKEY_PREFIX = (byte) 0xC0;

    private final static int ADDRESS_LENGTH = 20; // 1 + 1 + 20 + 1
    private final static int DEFAULT_PUBLICKEY_LENGTH = 32; // 1 + 1 + 32 + 1
    private final static int DEFAULT_PRIVATEKEY_LENGTH = 32; // 1 + 1 + 32 + 1
    private final static int SM2_PUBLICKEY_LENGTH = 65; // 1 + 1 + 65 + 1
    private final static int SM2_PRIVATEKEY_LENGTH = 32; // 1 + 1 + 32 + 1

    BubiKeyMember bubiKeyMember_ = new BubiKeyMember();

    public BubiKey(){

    }

    /**
     * 产生一个随机的私钥对象
     *
     * @param type
     * @throws Exception
     */
    public BubiKey(BubiKeyType type) throws Exception{
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
                SM2PublicKey pubKey = (SM2PublicKey) keypair.getPublic();
                SM2PrivateKey priKey = (SM2PrivateKey) keypair.getPrivate();
                bubiKeyMember_.setRaw_skey_(priKey.getD_Bytes());
                bubiKeyMember_.setRaw_pkey_(getSM2PublicKey(pubKey));
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

                KeyPair keyPair = keyPairGen.generateKeyPair();
                RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
                RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
                bubiKeyMember_.setRaw_skey_(privateKey.getEncoded());
                bubiKeyMember_.setRaw_pkey_(publicKey.getEncoded());
                break;
            }
            default:
                throw new Exception(BubiKeyType.CFCA == type ? "CFCA does not support this method" : "type does not exist");
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
    public BubiKey(CertFileType fileType, byte[] fileData, String filePwd) throws Exception{
        bubiKeyMember_.setType_(BubiKeyType.CFCA);
        switch (fileType) {
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
    public BubiKey(CertFileType fileType, String filePath, String filePwd) throws Exception{
        bubiKeyMember_.setType_(BubiKeyType.CFCA);
        switch (fileType) {
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
    public BubiKey(byte[] jksfileData, String jksfilePwd, String alias) throws Exception{
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
    public BubiKey(String jksfilePath, String jksfilePwd, String alias) throws Exception{
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
    public BubiKey(byte[] p7SignedData){
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
     *
     * @param bSkey 私钥字符串
     * @param bPkey 公钥字符串
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public BubiKey(String bSkey) throws Exception{
        this(bSkey, null);
    }

    /**
     * 传私钥和公钥构造，除CFCA外，其他公钥可为空；
     *
     * @param bSkey 私钥字符串
     * @param bPkey 公钥字符串
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public BubiKey(String bSkey, String bPkey) throws Exception{
        boolean[] isPkeyEmpty = new boolean[1];
        KeyFormatType keyFormatType = getKeyFormatType(bSkey, bPkey, null, isPkeyEmpty);

        BubiKeyMember keyMember = new BubiKeyMember();
        getPrivateKey(keyFormatType, bSkey, keyMember);
        if (!isPkeyEmpty[0]) {
            BubiKeyMember keyMember_pub = new BubiKeyMember();
            getPublicKey(keyFormatType, bPkey, keyMember_pub);
            if (keyMember.getType_() != keyMember_pub.getType_()) {
                throw new Exception("the private key does not match the public key, please check");
            }
            keyMember.setRaw_pkey_(keyMember_pub.getRaw_pkey_());
        }

        getPublicKey(isPkeyEmpty[0], keyMember);

        if (keyMember.getRaw_skey_() != null && keyMember.getRaw_pkey_() != null) {
            bubiKeyMember_.setType_(keyMember.getType_());
            bubiKeyMember_.setRaw_skey_(keyMember.getRaw_skey_());
            bubiKeyMember_.setRaw_pkey_(keyMember.getRaw_pkey_());
        } else {
            throw new Exception((keyMember.getRaw_skey_() == null ? "private key is invalid" : "public key is invalid") + "please check");
        }
    }

    /**
     * 对消息进行签名
     *
     * @param msg 要签名的消息
     * @return 签名内容
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public byte[] sign(byte[] msg) throws Exception{
        return sign(msg, bubiKeyMember_);
    }

    /**
     * 对消息进行签名
     *
     * @param msg   要签名的消息
     * @param bPkey 公钥
     * @return 签名内容
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static byte[] sign(byte[] msg, String bSkey) throws Exception{
        return sign(msg, bSkey, null);
    }

    /**
     * 对消息进行签名
     *
     * @param msg   要签名的消息
     * @param bSkey 私钥
     * @param bPkey 公钥
     * @return 签名内容
     * @throws SignatureException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     */
    public static byte[] sign(byte[] msg, String bSkey, String bPkey) throws Exception{
        boolean[] isPkeyEmpty = new boolean[1];
        KeyFormatType keyFormatType = getKeyFormatType(bSkey, bPkey, null, isPkeyEmpty);

        BubiKeyMember keyMember = new BubiKeyMember();
        getPrivateKey(keyFormatType, bSkey, keyMember);
        if (!isPkeyEmpty[0]) {
            BubiKeyMember keyMember_pub = new BubiKeyMember();
            getPublicKey(keyFormatType, bPkey, keyMember_pub);
            if (keyMember.getType_() != keyMember_pub.getType_()) {
                throw new Exception("the private key does not match the public key, please check");
            }
            keyMember.setRaw_pkey_(keyMember_pub.getRaw_pkey_());
        }

        getPublicKey(isPkeyEmpty[0], keyMember);
        return sign(msg, keyMember);
    }

    /**
     * 验证签名函数
     *
     * @param msg  消息内容
     * @param pkey 公钥字符串
     * @param sig  签名
     * @return
     * @throws Exception
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public boolean verify(byte[] msg, byte[] signMsg) throws Exception{
        boolean verifySuccess = false;
        verifySuccess = verify(msg, signMsg, bubiKeyMember_);

        return verifySuccess;
    }

    /**
     * 验证签名函数
     *
     * @param msg  消息内容
     * @param pkey 公钥字符串
     * @param sig  签名
     * @return
     * @throws Exception
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     */
    public static boolean verify(byte[] msg, byte[] signMsg, String pkey) throws Exception{
        KeyFormatType keyFormatType = getKeyFormatType(null, pkey, null, null);
        boolean verifySuccess = false;
        BubiKeyMember member = new BubiKeyMember();
        getPublicKey(keyFormatType, pkey, member);
        verifySuccess = verify(msg, signMsg, member);

        return verifySuccess;
    }

    /**
     * 根据私钥判断BubiKey的类型；
     *
     * @param bSkey 私钥字符串
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static BubiKeyType getBubiKeyType(String bSkey) throws Exception{
        KeyFormatType keyFormatType = getKeyFormatType(bSkey, null, null, null);
        BubiKeyType bubiKeyType = null;
        byte[] skey = null;
        switch (keyFormatType) {
            case B58:
                skey = Base58.decode(bSkey);
                break;
            default:
                throw new Exception(keyFormatType + " does not exist");
        }

        if (skey.length <= 9) {
            throw new Exception("the Base58 PrivateKey is invalid");
        }
        // 3字节前缀，1字节类型，n字节私钥，4字节校验码

        bubiKeyType = BubiKeyType.values()[skey[3] - 1];

        return bubiKeyType;
    }

    /**
     * 返回base58编码的bubi地址
     *
     * @return
     * @throws Exception
     */
    public String getB58Address() throws Exception{
        byte[] raw_pkey = bubiKeyMember_.getRaw_pkey_();
        if (null == raw_pkey) {
            throw new Exception("public key is null");
        }

        return b58Address(bubiKeyMember_.getType_(), raw_pkey);
    }

    /**
     * @return 返回base58编码的公钥
     * @throws Exception
     */
    public String getB58PublicKey() throws Exception{
        return b58PublicKey(bubiKeyMember_.getType_(), bubiKeyMember_.getRaw_pkey_());
    }


    /**
     * @return 返回b58编码格式的私钥
     * @throws Exception
     */
    public String getB58PrivKey() throws Exception{
        return b58PrivateKey(bubiKeyMember_.getType_(), bubiKeyMember_.getRaw_skey_());
    }

    /**
     * @return 返回16进制编码格式的私钥
     * @throws Exception
     */
    public String getB16PrivKey() throws Exception{
        return b16PrivateKey(bubiKeyMember_.getType_(), bubiKeyMember_.getRaw_skey_()).toLowerCase();
    }

    /**
     * @return 返回16进制编码格式的公钥
     * @throws Exception
     */
    public String getB16PublicKey() throws Exception{
        return b16PublicKey(bubiKeyMember_.getType_(), bubiKeyMember_.getRaw_pkey_()).toLowerCase();
    }

    /**
     * @return 返回16进制编码格式的地址
     * @throws Exception
     */
    public String getB16Address() throws Exception{
        byte[] raw_pkey = bubiKeyMember_.getRaw_pkey_();
        if (null == raw_pkey) {
            throw new Exception("public key is null");
        }

        return b16Address(bubiKeyMember_.getType_(), raw_pkey).toLowerCase();
    }

    /**
     * 根据私钥计算公钥，CFCA不支持
     *
     * @param pKey 私钥字符串
     * @return 编码后公钥
     */
    public static String getB58PublicKey(String bSkey) throws Exception{
        BubiKeyMember member = new BubiKeyMember();
        KeyFormatType keyFormatType = getKeyFormatType(bSkey, null, null, null);
        getPrivateKey(keyFormatType, bSkey, member);
        getPublicKey(true, member);
        return b58PublicKey(member.getType_(), member.getRaw_pkey_());
    }

    /**
     * 根据私钥计算公钥，CFCA不支持
     *
     * @param pKey 私钥字符串
     * @return 编码后公钥
     */
    public static String getB16PublicKey(String bSkey) throws Exception{
        BubiKeyMember member = new BubiKeyMember();
        KeyFormatType keyFormatType = getKeyFormatType(bSkey, null, null, null);
        getPrivateKey(keyFormatType, bSkey, member);
        getPublicKey(true, member);
        return b16PublicKey(member.getType_(), member.getRaw_pkey_()).toLowerCase();
    }

    /**
     * 根据公钥计算bubi地址
     *
     * @param pKey 公钥字符串
     * @return bubi地址
     */
    public static String getB58Address(String pKey) throws Exception{
        KeyFormatType keyFormatType = getKeyFormatType(null, pKey, null, null);
        BubiKeyMember member = new BubiKeyMember();
        getPublicKey(keyFormatType, pKey, member);

        return b58Address(member.getType_(), member.getRaw_pkey_());
    }

    /**
     * 根据公钥计算bubi地址
     *
     * @param pKey 公钥字符串
     * @return bubi地址
     */
    public static String getB16Address(String pKey) throws Exception{
        KeyFormatType keyFormatType = getKeyFormatType(null, pKey, null, null);
        BubiKeyMember member = new BubiKeyMember();
        getPublicKey(keyFormatType, pKey, member);

        return b16Address(member.getType_(), member.getRaw_pkey_()).toLowerCase();
    }

    private static void getPrivateKey(KeyFormatType keyFormatType, String bSkey, BubiKeyMember member) throws Exception{
        byte[] rawSKey = null;
        BubiKeyType type = null;
        switch (keyFormatType) {
            case B58: {
                byte[] skey = Base58.decode(bSkey);
                rawSKey = new byte[skey.length - 9];

                if (skey.length <= 9) {
                    throw new Exception("the Base58 PrivateKey is invalid");
                }
                // 3字节前缀，1字节类型，n字节私钥，4字节校验码
                if (skey[3] > 4 || skey[3] < 1) {
                    throw new Exception("the Base58 PrivateKey is invalid");
                }
                System.arraycopy(skey, 4, rawSKey, 0, rawSKey.length);
                type = BubiKeyType.values()[skey[3] - 1];

                break;
            }
            case B16: {
                BubiKeyType[] type_priv = new BubiKeyType[1];
                byte[] buffSKey = HexFormat.hexToByte(bSkey);
                if (buffSKey.length < 3) {
                    throw new Exception("private key (" + bSkey + ") is invalid, please check");
                }
                rawSKey = new byte[buffSKey.length - 3];
                if (!getKeyElement(buffSKey, rawSKey, type_priv)) {
                    throw new Exception("private key (" + bSkey + ") is invalid, please check");
                }
                type = type_priv[0];
                break;
            }
            default:
                throw new Exception(keyFormatType + " does not exist");
        }

        member.setType_(type);
        member.setRaw_skey_(rawSKey);
    }

    private static void getPublicKey(KeyFormatType keyFormatType, String bPkey, BubiKeyMember member) throws Exception{
        byte[] rawPKey = null;
        BubiKeyType type = null;
        switch (keyFormatType) {
            case B58: {
                byte[] pkey = Base58.decode(bPkey);
                rawPKey = new byte[pkey.length - 4];
                if (pkey.length == 32) {
                    member.setType_(BubiKeyType.ED25519);
                    member.setRaw_pkey_(pkey);
                    return;
                }

                if (pkey[0] > 4 || pkey[0] < 1) {
                    throw new Exception("the Base58 PublicKey is invalid");
                }

                System.arraycopy(pkey, 4, rawPKey, 0, pkey.length - 4);
                type = BubiKeyType.values()[pkey[0] - 1];
                break;
            }
            case B16: {
                BubiKeyType[] type_pub = new BubiKeyType[1];
                byte[] buffPKey = HexFormat.hexToByte(bPkey);
                if (buffPKey.length < 3) {
                    throw new Exception("public key (" + bPkey + ") is invalid, please check");
                }
                rawPKey = new byte[buffPKey.length - 3];
                if (!getKeyElement(buffPKey, rawPKey, type_pub)) {
                    throw new Exception("public key (" + bPkey + ") is invalid, please check");
                }
                type = type_pub[0];
                break;
            }
            default:
                throw new Exception(keyFormatType + " does not exist");
        }

        member.setRaw_pkey_(rawPKey);
        member.setType_(type);
    }

    private static void getPublicKey(boolean isPkeyEmpty, BubiKeyMember member) throws Exception{
        byte[] rawSkey = member.getRaw_skey_();
        byte[] rawPkey = member.getRaw_pkey_();

        switch (member.getType_()) {
            case ED25519: {
                EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName("ed25519-sha-512");
                EdDSAPrivateKeySpec privKey = new EdDSAPrivateKeySpec(rawSkey, spec);
                EdDSAPublicKeySpec spec2 = new EdDSAPublicKeySpec(privKey.getA(), spec);
                EdDSAPublicKey pDsaPublicKey = new EdDSAPublicKey(spec2);
                if (isPkeyEmpty) {
                    member.setRaw_pkey_(pDsaPublicKey.getAbyte());
                } else if (!Arrays.areEqual(pDsaPublicKey.getAbyte(), rawPkey)) {
                    throw new Exception("the private key does not match the public key, please check");
                }
                break;
            }
            case ECCSM2: {
                SM2PrivateKey privateKey = new SM2PrivateKey(rawSkey);
                SM2PublicKey publicKey = privateKey.getSM2PublicKey();
                if (isPkeyEmpty) {
                    member.setRaw_pkey_(getSM2PublicKey(publicKey));
                } else if (!Arrays.areEqual(getSM2PublicKey(publicKey), rawPkey)) {
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
                } else if (!Arrays.areEqual(myPublicKey.getEncoded(), rawPkey)) {
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
                    privateKey = new SM2PrivateKey(rawSkey);
                    SM2PublicKey sm2PublicKey = (SM2PublicKey) x509Cert.getPublicKey();
                    if (!Arrays.areEqual(((SM2PrivateKey) privateKey).getSM2PublicKey().getEncoded(), sm2PublicKey.getEncoded())) {
                        throw new Exception("the private key does not match the public key, please check");
                    }
                } else {
                    PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(rawSkey);

                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                    privateKey = (RSAPrivateKey) keyFactory.generatePrivate(privatePKCS8);

                    RSAPrivateCrtKey privk = (RSAPrivateCrtKey) privateKey;
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

    private static KeyFormatType getKeyFormatType(String bSkey, String bPkey, boolean[] isSkeyEmptys, boolean[] isPkeyEmptys) throws Exception{
        boolean isPkeyEmpty = false;
        boolean isPkeyHex = false;
        if (null == bPkey || bPkey.isEmpty()) {
            isPkeyEmpty = true;
        } else {
            isPkeyHex = HexFormat.isHexString(bPkey);
        }

        boolean isSkeyEmpty = false;
        boolean isSkeyHex = false;
        if (bSkey != null && !bSkey.isEmpty()) {
            isSkeyHex = HexFormat.isHexString(bSkey);
        } else {
            isSkeyEmpty = true;
        }

        KeyFormatType keyFormatType = null;
        if (!isPkeyEmpty && !isSkeyEmpty && ((isSkeyHex && !isPkeyHex) || (!isSkeyHex && isPkeyHex))) {
            throw new Exception("the private key does not match the public key, please check");
        } else if (isSkeyEmpty) {
            keyFormatType = isPkeyHex ? KeyFormatType.B16 : KeyFormatType.B58;
        } else {
            keyFormatType = isSkeyHex ? KeyFormatType.B16 : KeyFormatType.B58;
        }

        if (isPkeyEmptys != null) {
            isPkeyEmptys[0] = isPkeyEmpty;
        }
        if (isSkeyEmptys != null) {
            isSkeyEmptys[0] = isSkeyEmpty;
        }

        return keyFormatType;
    }

    private static byte[] sign(byte[] msg, BubiKeyMember keyMember) throws Exception{
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

                SM2PrivateKey privateKey = KeyUtil.getSM2PrivateKey(rawSkey, null, null);
                SM2PublicKey publicKey = getSM2PublicKey(rawPkey);
                final byte[] userId = "1234567812345678".getBytes("UTF8");
                final String signAlg = Mechanism.SM3_SM2;
                //
                byte[] hash = HashUtil.SM2HashMessageByBCWithZValue(userId, msg, publicKey.getPubXByInt(), publicKey.getPubYByInt());
                signMessage = ASN1toRS(Base64.decode(signature.p1SignByHash(signAlg, hash, privateKey, session)));
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
                    privateKey = KeyUtil.getSM2PrivateKey(rawSkey, null, null);
                    SM2PublicKey sm2PublicKey = (SM2PublicKey) x509Cert.getPublicKey();

                    final byte[] userId = "1234567812345678".getBytes("UTF8");
                    hash = HashUtil.SM2HashMessageByBCWithZValue(userId, msg,
                            sm2PublicKey.getPubXByInt(), sm2PublicKey.getPubYByInt());// SM2签名包含Z值

                    signAlg = Mechanism.SM3_SM2;
                } else {
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

    private static boolean verify(byte[] msg, byte[] sign, BubiKeyMember member) throws Exception{
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

                SM2PublicKey publicKey = getSM2PublicKey(rawpkey);
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

                Signature signature = Signature.getInstance("SHA1WithRSA");

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
                    SM2PublicKey publicKey = (SM2PublicKey) x509Cert.getPublicKey();

                    final byte[] userId = "1234567812345678".getBytes("UTF8");
                    hash = HashUtil.SM2HashMessageByBCWithZValue(userId, msg,
                            publicKey.getPubXByInt(), publicKey.getPubYByInt());// SM2签名包含Z值
                } else {
                    hash = HashUtil.RSAHashMessageByBC(msg, new Mechanism(Mechanism.SHA256), false);
                }

                verifySuccess = signature.p7VerifyByHash(hash, sign, session);
                break;
            }
        }
        return verifySuccess;
    }

    private static String b58PrivateKey(BubiKeyType type, byte[] raw_skey) throws Exception{
        if (null == raw_skey) {
            throw new Exception("private key is null");
        }
        String b58PrivateKey = null;
        byte[] tmp = null;
        byte[] buff = new byte[raw_skey.length + 5];
        buff[0] = (byte) 0xDA;
        buff[1] = (byte) 0x37;
        buff[2] = (byte) 0x9F;
        System.arraycopy(raw_skey, 0, buff, 4, raw_skey.length);
        byte[] hash1 = null;
        byte[] hash2 = null;

        buff[3] = (byte) (type.ordinal() + 1);

        if (type != BubiKeyType.ECCSM2) {
            MessageDigest sha256 = null;
            try {
                sha256 = MessageDigest.getInstance("SHA-256");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            sha256.update(buff);
            hash1 = sha256.digest();

            sha256.reset();
            sha256.update(hash1);
            hash2 = sha256.digest();
        } else {
            hash1 = SM3Digest.Hash(buff);
            hash2 = SM3Digest.Hash(hash1);
        }

        tmp = new byte[buff.length + 4];

        System.arraycopy(buff, 0, tmp, 0, buff.length);
        System.arraycopy(hash2, 0, tmp, buff.length, 4);
        b58PrivateKey = Base58.encode(tmp);

        return b58PrivateKey;
    }

    private static String b16PrivateKey(BubiKeyType type, byte[] raw_skey) throws Exception{
        if (null == raw_skey) {
            throw new Exception("private key is null");
        }

        int length = raw_skey.length + 3;
        byte[] buff = new byte[length];
        buff[0] = PRIVATEKEY_PREFIX;
        buff[1] = (byte) (type.ordinal() + 1);

        System.arraycopy(raw_skey, 0, buff, 2, raw_skey.length);

        buff[length - 1] = CRC8.calcCrc8(buff, 0, length - 1);

        return HexFormat.byteToHex(buff);
    }

    private static String b58PublicKey(BubiKeyType type, byte[] raw_pkey) throws Exception{
        if (null == raw_pkey) {
            throw new Exception("public key is null");
        }

        if (type == BubiKeyType.ED25519) {
            return Base58.encode(raw_pkey);
        }

        byte[] buff = new byte[raw_pkey.length + 4];
        buff[0] = (byte) (type.ordinal() + 1);

        System.arraycopy(raw_pkey, 0, buff, 4, raw_pkey.length);

        String bs58PublicKey = null;
        bs58PublicKey = Base58.encode(buff);

        return bs58PublicKey;
    }

    private static String b16PublicKey(BubiKeyType type, byte[] raw_pkey) throws Exception{
        if (null == raw_pkey) {
            throw new Exception("public key is null");
        }
        int length = raw_pkey.length + 3;
        byte[] buff = new byte[length];
        buff[0] = PUBLICKEY_PREFIX;
        buff[1] = (byte) (type.ordinal() + 1);

        System.arraycopy(raw_pkey, 0, buff, 2, raw_pkey.length);

        buff[length - 1] = CRC8.calcCrc8(buff, 0, length - 1);

        return HexFormat.byteToHex(buff);
    }

    private static String b58Address(BubiKeyType bubiKeyType, byte[] raw_pkey){
        byte[] buff = new byte[25];
        buff[0] = (byte) 0xE6;
        buff[1] = (byte) 0x9A;
        buff[2] = (byte) 0x73;
        buff[3] = (byte) 0xFF;

        // 初始化MessageDigest
        MessageDigest md160 = null;
        try {
            Security.addProvider(new BouncyCastleProvider());
            md160 = MessageDigest.getInstance("RIPEMD160");
            // 执行消息摘要
            byte[] pkey160 = md160.digest(raw_pkey);
            System.arraycopy(pkey160, 0, buff, 5, 20);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        byte[] hash1 = null;
        byte[] hash2 = null;
        if (BubiKeyType.ECCSM2 == bubiKeyType) {
            hash1 = SM3Digest.Hash(buff);
            hash2 = SM3Digest.Hash(hash1);
        } else {
            MessageDigest sha256 = null;
            try {
                sha256 = MessageDigest.getInstance("SHA-256");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            sha256.update(buff, 0, 25);
            hash1 = sha256.digest();
            sha256.reset();
            sha256.update(hash1);
            hash2 = sha256.digest();
        }
        byte[] result = new byte[29];
        System.arraycopy(buff, 0, result, 0, 25);
        System.arraycopy(hash2, 0, result, 25, 4);
        return Base58.encode(result);
    }

    private static String b16Address(BubiKeyType bubiKeyType, byte[] rawPkey){
        int length = 23;
        byte[] buff = new byte[length];
        buff[0] = ADDRESS_PREFIX;
        buff[1] = (byte) (bubiKeyType.ordinal() + 1);

        byte[] hash = null;
        if (bubiKeyType != BubiKeyType.ECCSM2) {
            MessageDigest sha256 = null;
            try {
                sha256 = MessageDigest.getInstance("SHA-256");

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            sha256.update(rawPkey);
            hash = sha256.digest();
        } else {
            hash = SM3Digest.Hash(rawPkey);
        }

        System.arraycopy(hash, 12, buff, 2, hash.length - 12);

        buff[length - 1] = CRC8.calcCrc8(buff, 0, length - 1);

        return HexFormat.byteToHex(buff);
    }

    private static boolean getKeyElement(byte[] buff, byte[] rawKey, BubiKeyType[] bubiKeyType){
        boolean bret = false;
        do {
            byte prefix = buff[0];
            BubiKeyType type = BubiKeyType.values()[buff[1] - 1];

            int dataLen = buff.length - 3;
            int checkSum = buff[buff.length - 1];
            int calcCheckSum = CRC8.calcCrc8(buff, 0, buff.length - 1);
            if (checkSum != calcCheckSum) {
                break;
            }


            if (prefix == ADDRESS_PREFIX) {
                switch (type) {
                    case ED25519:
                    case ECCSM2:
                        bret = (ADDRESS_LENGTH == dataLen);
                        break;
                    case RSA:
                    case CFCA:
                        bret = true;
                        break;
                    default:
                        break;
                }
            } else if (prefix == PUBLICKEY_PREFIX) {
                switch (type) {
                    case ED25519:
                        bret = (DEFAULT_PUBLICKEY_LENGTH == dataLen);
                        break;
                    case ECCSM2:
                        bret = (SM2_PUBLICKEY_LENGTH == dataLen);
                        break;
                    case RSA:
                    case CFCA:
                        bret = true;
                        break;
                    default:
                        break;
                }
            } else if (prefix == PRIVATEKEY_PREFIX) {
                switch (type) {
                    case ED25519:
                        bret = (DEFAULT_PRIVATEKEY_LENGTH == dataLen);
                        break;
                    case ECCSM2:
                        bret = (SM2_PRIVATEKEY_LENGTH == dataLen);
                        break;
                    case RSA:
                    case CFCA:
                        bret = true;
                        break;
                    default:
                        break;
                }
            }

            if (bret) {
                bubiKeyType[0] = type;
                System.arraycopy(buff, 2, rawKey, 0, buff.length - 3);
            }

        } while (false);


        return bret;
    }

    private static byte[] getSM2PublicKey(SM2PublicKey pubKey){
        byte[] raw_pkey = new byte[65];
        byte[] x = pubKey.getPubX();
        byte[] y = pubKey.getPubY();

        raw_pkey[0] = 4;
        System.arraycopy(x, 0, raw_pkey, 1, 32);
        System.arraycopy(y, 0, raw_pkey, 33, 32);

        return raw_pkey;
    }

    private static SM2PublicKey getSM2PublicKey(byte[] raw_pkey){
        byte[] x = new byte[32];
        byte[] y = new byte[32];
        System.arraycopy(raw_pkey, 1, x, 0, 32);
        System.arraycopy(raw_pkey, 33, y, 0, 32);

        SM2PublicKey publicKey = new SM2PublicKey(x, y);

        return publicKey;
    }

    private static byte[] ASN1toRS(byte[] asn1RS){
        ASN1Sequence sequence = ASN1Sequence.getInstance(asn1RS);
        ASN1Integer R = (ASN1Integer) sequence.getObjectAt(0);
        ASN1Integer S = (ASN1Integer) sequence.getObjectAt(1);
        byte[] r = BigIntegerUtil.asUnsigned32ByteArray(R.getPositiveValue());
        byte[] s = BigIntegerUtil.asUnsigned32ByteArray(S.getPositiveValue());
        byte[] signature = new byte[64];
        System.arraycopy(r, 0, signature, 0, 32);
        System.arraycopy(s, 0, signature, 32, 32);
        return signature;
    }
}
