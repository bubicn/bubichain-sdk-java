package cn.bubi.sdk.core.transaction.model;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 下午6:09.
 */
public class Signature{

    private String publicKey;// 公钥
    private String privateKey;// 私钥


    public Signature(String publicKey, String privateKey){
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey(){
        return publicKey;
    }

    public void setPublicKey(String publicKey){
        this.publicKey = publicKey;
    }

    public String getPrivateKey(){
        return privateKey;
    }

    public void setPrivateKey(String privateKey){
        this.privateKey = privateKey;
    }
}
