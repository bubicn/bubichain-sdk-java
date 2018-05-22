package cn.bubi.access.utils.security;

import cn.bubi.encryption.BubiKey;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * 摘要工具类
 *
 * @author haiq
 */
public class ShaUtils{

    /**
     * 对指定的字节数组进行 SHA128 哈希；
     *
     * @param data
     * @return 返回长度为 16 的字节数组；
     */
    public static byte[] hash_128(byte[] bytes){
        byte[] hash256Bytes = hash_256(bytes);
        return Arrays.copyOf(hash256Bytes, 16);
    }

    /**
     * 对指定的字节数组进行 SHA256 哈希；
     *
     * @param data
     * @return 返回长度为 32 的字节数组；
     */
    public static byte[] hash_256(byte[] bytes){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(bytes);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public static byte[] hash_256(InputStream input){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
            byte[] buff = new byte[64];
            int len = 0;
            while ((len = input.read(buff)) > 0) {
                md.update(buff, 0, len);
            }
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e.getMessage(), e);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * 组装待签名数据
     */
    @Deprecated
    public static byte[] getData(byte[] userAgent, byte[] url, byte[] requestBody){

        byte[] bytes = new byte[userAgent.length + url.length + (requestBody == null ? 0 : requestBody.length)];
        System.arraycopy(userAgent, 0, bytes, 0, userAgent.length);
        System.arraycopy(url, 0, bytes, userAgent.length, url.length);
        if (requestBody != null) {
            System.arraycopy(requestBody, 0, bytes, userAgent.length + url.length, requestBody.length);
        }
        return bytes;
    }


    /**
     * 签名3.0
     *
     * @param msg   要签名的消息
     * @param bSkey 私钥
     * @return 签名内容
     */
    public static byte[] signV3(byte[] msg, String bSkey, String pbKey){
        try {
            BubiKey bubiKey = new BubiKey(bSkey, pbKey);
            return bubiKey.sign(msg);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Signature failed !!", e);
        }
    }

    /**
     * 验签3.0
     *
     * @param msg  消息内容
     * @param pkey 公钥字符串
     * @param sig  签名
     */
    public static boolean verifyV3(byte[] msg, byte[] signMsg, String pkey){
        try {
            return BubiKey.verify(msg, signMsg, pkey);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Signature verify failed !!", e);
        }
    }

}
