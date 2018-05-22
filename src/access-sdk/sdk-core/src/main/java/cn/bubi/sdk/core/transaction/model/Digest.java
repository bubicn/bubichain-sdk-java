package cn.bubi.sdk.core.transaction.model;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/30 下午5:18.
 */
public class Digest{

    private String publicKey;// 公钥
    private byte[] originDigest;// 摘要

    public Digest(String publicKey, byte[] originDigest){
        this.publicKey = publicKey;
        this.originDigest = originDigest;
    }

    public String getPublicKey(){
        return publicKey;
    }

    public void setPublicKey(String publicKey){
        this.publicKey = publicKey;
    }

    public byte[] getOriginDigest(){
        return originDigest;
    }

    public void setOriginDigest(byte[] originDigest){
        this.originDigest = originDigest;
    }
}
