package cn.bubi.access.adaptation.blockchain.bc.response.operation;

import cn.bubi.access.adaptation.blockchain.bc.response.Priv;
import cn.bubi.access.adaptation.blockchain.bc.response.contract.Contract;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建账号
 */
public class CreateAccount{

    private String metadata;
    @JSONField(name = "dest_address")
    private String destAddress;

    private Contract contract = new Contract();// 合约
    private List<SetMetadata> metadatas = new ArrayList<>();// metadata
    private Priv priv = new Priv();// 权限


    public Contract getContract(){
        return contract;
    }

    public void setContract(Contract contract){
        this.contract = contract;
    }

    public List<SetMetadata> getMetadatas(){
        return metadatas;
    }

    public void setMetadatas(List<SetMetadata> metadatas){
        this.metadatas = metadatas;
    }

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

    public Priv getPriv(){
        return priv;
    }

    public void setPriv(Priv priv){
        this.priv = priv;
    }


}
