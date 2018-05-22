package cn.bubi.access.adaptation.blockchain.bc.response.operation;

import cn.bubi.access.adaptation.blockchain.bc.response.Asset;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 转移资产
 *
 * @author 陈志平
 */
public class Payment{
    private String metadata;
    @JSONField(name = "dest_address")
    private String destAddress;
    private Asset asset;

    public String getMetadata(){
        return metadata;
    }

    public void setMetadata(String metadata){
        this.metadata = metadata;
    }

    public String getDestAddress(){
        return destAddress;
    }

    public void setDestAddress(String destAddress){
        this.destAddress = destAddress;
    }

    public Asset getAsset(){
        return asset;
    }

    public void setAsset(Asset asset){
        this.asset = asset;
    }
}
