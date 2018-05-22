package cn.bubi.access.adaptation.blockchain.bc.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/30 上午11:58.
 */
public class Hello{

    @JSONField(name = "bubi_version")
    private String bubiVersion;

    @JSONField(name = "current_time")
    private String currentTime;

    @JSONField(name = "hash_type")
    private Integer hashType;

    @JSONField(name = "ledger_version")
    private String ledgerVersion;

    @JSONField(name = "overlay_version")
    private String overlayVersion;

    @JSONField(name = "websocket_address")
    private String websocketAddress;

    public String getBubiVersion(){
        return bubiVersion;
    }

    public void setBubiVersion(String bubiVersion){
        this.bubiVersion = bubiVersion;
    }

    public String getCurrentTime(){
        return currentTime;
    }

    public void setCurrentTime(String currentTime){
        this.currentTime = currentTime;
    }

    public Integer getHashType(){
        return hashType;
    }

    public void setHashType(Integer hashType){
        this.hashType = hashType;
    }

    public String getLedgerVersion(){
        return ledgerVersion;
    }

    public void setLedgerVersion(String ledgerVersion){
        this.ledgerVersion = ledgerVersion;
    }

    public String getOverlayVersion(){
        return overlayVersion;
    }

    public void setOverlayVersion(String overlayVersion){
        this.overlayVersion = overlayVersion;
    }

    public String getWebsocketAddress(){
        return websocketAddress;
    }

    public void setWebsocketAddress(String websocketAddress){
        this.websocketAddress = websocketAddress;
    }

    @Override
    public String toString(){
        return "Hello{" +
                "bubiVersion='" + bubiVersion + '\'' +
                ", currentTime='" + currentTime + '\'' +
                ", hashType=" + hashType +
                ", ledgerVersion='" + ledgerVersion + '\'' +
                ", overlayVersion='" + overlayVersion + '\'' +
                ", websocketAddress='" + websocketAddress + '\'' +
                '}';
    }
}
