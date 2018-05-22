package cn.bubi.access.utils.blockchain;

import cn.bubi.access.utils.codec.Base58Utils;
import cn.bubi.encryption.BubiKey;
import cn.bubi.encryption.BubiKeyType;
import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;
import net.i2p.crypto.eddsa.KeyPairGenerator;
import net.i2p.crypto.eddsa.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public abstract class BlockchainKeyPairFactory{

    private static KeyPairGenerator keyPairGenerator = new KeyPairGenerator();

    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    private BlockchainKeyPairFactory(){
    }

    /**
     * 生成随机的布比信息{布比私钥，布比公钥，布比地址} 默认的Type = BubiKeyType.ED25519
     */
    public static BlockchainKeyPair generateBubiKeyPair(){
        return generateBubiKeyPair(BubiKeyType.ED25519);
    }

    public static String getPublicKeyV3(String privateKey){
        try {
            return new BubiKey(privateKey).getB16PublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * 生成随机的布比信息{布比私钥，布比公钥，布比地址} for 3.0
     *
     * @param type
     * @return
     */
    public static BlockchainKeyPair generateBubiKeyPair(BubiKeyType type){
        try {

            BubiKey bubiKey = new BubiKey(type);
            String publicKey = bubiKey.getB16PublicKey();
            String privateKey = bubiKey.getB16PrivKey();
            String address = bubiKey.getB16Address();
            return new BlockchainKeyPair(privateKey, publicKey, address);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据ED25519算法生成的私钥进行加工，从而得到布比私钥
     * 1.将3字节前缀和1字节版本号0XDA379F01加到P前面，1字节压缩标志添加到P后面即 M=0XDA379F01 + P+0X00
     * 2.将M用SHA256计算两次取前4字节，即Checksum=SHA256(SHA256(M)) 的前4字节
     * 3.将Checksum的前四字节加到M后面，即S=M+Checksum
     * 4.对S进行Base58编码即得到布比私钥。privxxxxxxxxxxxxxxxxxxxxxxxx
     *
     * @param priKey
     * @return
     */
    private static String generateBubiPriKey(EdDSAPrivateKey priKey){
        try {
            byte[] priKeyheadArr = Utils.hexToBytes("DA379F01");
            byte[] M = ArrayUtils.addAll(priKeyheadArr, priKey.getSeed());
            byte[] priKeyendArr = Utils.hexToBytes("00");
            M = ArrayUtils.addAll(M, priKeyendArr);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(M);
            byte[] m_256_1 = md.digest();
            md.update(m_256_1);
            byte[] m_256_2 = md.digest();
            byte[] M_check = new byte[M.length + 4];
            System.arraycopy(M, 0, M_check, 0, M.length);
            System.arraycopy(m_256_2, 0, M_check, M.length, 4);
            return Base58Utils.encode(M_check);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occured on generating BubiAddress!--" + e.getMessage(), e);
        }
    }

    /**
     * 获取布比公钥，对ED25519生成的公钥进行base58编码即可
     *
     * @param pubKey
     * @return
     */
    private static String generateBubiPubKey(EdDSAPublicKey pubKey){
        return Base58Utils.encode(pubKey.getAbyte());
    }

    /**
     * 根据公钥生成布比地址 1.对Q进行RIPEMD160算法得到20字节的N，即N = RIPEMD160（Q）
     * 2.在N前面加4字节前缀和1字节版本号。即M=0XE69A73FF01+N
     * 3.对M进行两次SHA256算法取前四字节，即Checksum=SHA256(SHA256(M))的前4字节
     * 4.将Checksum加到M后面，即S=M+Checksum 5.对S进行Base58编码即得到布比地址bubixxxxxxxxxxxx
     *
     * @param pubKey
     * @return 布比地址字符串
     */
    private static String generateBubiAddress(EdDSAPublicKey pubKey){
        try {
            MessageDigest md = MessageDigest.getInstance("RIPEMD160");
            md.update(pubKey.getAbyte());
            byte[] N = md.digest();

            byte[] pubKeyheadArr = Utils.hexToBytes("E69A73FF01");
            byte[] M = ArrayUtils.addAll(pubKeyheadArr, N);
            md = MessageDigest.getInstance("SHA-256");
            md.update(M);
            byte[] M_256_1 = md.digest();
            md.update(M_256_1);
            byte[] M_256_2 = md.digest();
            byte[] S = new byte[M.length + 4];
            System.arraycopy(M, 0, S, 0, M.length);
            System.arraycopy(M_256_2, 0, S, M.length, 4);
            return Base58Utils.encode(S);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error occured on generating BubiAddress!--" + e.getMessage(), e);
        }
    }

    /**
     * 将base58编码的私钥转换为非对称加密算法需要的字节数组
     *
     * @param base58PrivKeyStr
     * @return
     */
    public static byte[] getPriKeyBytes(String base58PrivKeyStr){
        byte[] base58PrivKey = Base58Utils.decode(base58PrivKeyStr);
        if (base58PrivKey == null || base58PrivKey.length != 41) {
            throw new RuntimeException("私钥[" + base58PrivKey + "]长度不正确！");
        }
        byte[] privArr = new byte[32];
        System.arraycopy(base58PrivKey, 4, privArr, 0, privArr.length);
        return privArr;
    }
}
