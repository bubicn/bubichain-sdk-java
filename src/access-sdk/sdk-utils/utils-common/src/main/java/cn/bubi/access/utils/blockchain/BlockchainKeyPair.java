package cn.bubi.access.utils.blockchain;

import java.io.Serializable;

public class BlockchainKeyPair implements Serializable{

    private static final long serialVersionUID = -1758433189743575436L;

    //私钥字串
    private String priKey;

    //公钥字串
    private String pubKey;

    //布比地址字串
    private String bubiAddress;


    public BlockchainKeyPair(String priKey, String pubKey, String bubiAddress){
        super();
        this.priKey = priKey;
        this.pubKey = pubKey;
        this.bubiAddress = bubiAddress;
    }

    public BlockchainKeyPair(){
        super();
    }

    public String getPriKey(){
        return priKey;
    }

    public void setPriKey(String priKey){
        this.priKey = priKey;
    }

    public String getPubKey(){
        return pubKey;
    }

    public void setPubKey(String pubKey){
        this.pubKey = pubKey;
    }

    public String getBubiAddress(){
        return bubiAddress;
    }

    public void setBubiAddress(String bubiAddress){
        this.bubiAddress = bubiAddress;
    }

    @Override
    public String toString(){
        return "BubiInfo [priKey=" + priKey + ", pubKey=" + pubKey + ", bubiAddress=" + bubiAddress + "]";
    }


}
