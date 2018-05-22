package cn.bubi.access.adaptation.blockchain.bc.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 交易信息
 *
 * @author 陈志平
 */
public class SubTransaction{
    @JSONField(name = "source_address")
    private String sourceAddress;
    private long nonce;
    private String metadata;
    private Operation[] operations;


    public String getMetadata(){
        return metadata;
    }

    public void setMetadata(String metadata){
        this.metadata = metadata;
    }

    public String getSourceAddress(){
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress){
        this.sourceAddress = sourceAddress;
    }

    public long getNonce(){
        return nonce;
    }

    public void setNonce(long nonce){
        this.nonce = nonce;
    }

    public Operation[] getOperations(){
        return operations;
    }

    public void setOperations(Operation[] operations){
        this.operations = operations;
    }


}
