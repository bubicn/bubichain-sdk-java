package cn.bubi.access.adaptation.blockchain.bc.response.operation;

import cn.bubi.access.adaptation.blockchain.bc.response.TypeThreshold;
import com.alibaba.fastjson.annotation.JSONField;

/**
 * 设置门限
 *
 * @author 陈志平
 */
public class SetThreshold{
    @JSONField(name = "tx_threshold")
    private long txThreshold;
    @JSONField(name = "type_thresholds")
    private TypeThreshold[] typeThresholds;

    public long getTxThreshold(){
        return txThreshold;
    }

    public void setTxThreshold(long txThreshold){
        this.txThreshold = txThreshold;
    }

    public TypeThreshold[] getTypeThresholds(){
        return typeThresholds;
    }

    public void setTypeThresholds(TypeThreshold[] typeThresholds){
        this.typeThresholds = typeThresholds;
    }

}
