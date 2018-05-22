package cn.bubi.sdk.core.operation;

import cn.bubi.access.utils.spring.StringUtils;
import cn.bubi.blockchain.adapter3.Chain;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.utils.SwallowUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:51.
 */
public abstract class AbstractBcOperation implements BcOperation{

    private int type;
    private String operationSourceAddress;
    private String operationMetadata;

    protected AbstractBcOperation(int type){
        this.type = type;
    }

    @Override
    public void buildTransaction(Chain.Transaction.Builder builder, long maxSeq) throws SdkException{
        Chain.Operation.Builder operation = builder.addOperationsBuilder();
        operation.setType(Chain.Operation.Type.valueOf(type));
        operation.setExprCondition("LEDGER_SEQ <= " + maxSeq);
        if (!StringUtils.isEmpty(operationSourceAddress))
            operation.setSourceAddress(operationSourceAddress);
        if (!StringUtils.isEmpty(operationMetadata))
            operation.setMetadata(ByteString.copyFrom(SwallowUtil.getBytes(operationMetadata)));
        buildOperation(operation);
    }

    private void buildOperation(Chain.Operation.Builder operation) throws SdkException{
        buildOperationContinue(operation);
    }

    @Override
    public BcOperation generateOperation(JSONObject originJson){
        // todo 反向生成操作对象，现没有需求，暂不实现
        return null;
    }

    /**
     * 子类继续build
     */
    protected abstract void buildOperationContinue(Chain.Operation.Builder operation);


    public String getOperationSourceAddress(){
        return operationSourceAddress;
    }

    public void setOperationSourceAddress(String operationSourceAddress){
        this.operationSourceAddress = operationSourceAddress;
    }

    public String getOperationMetadata(){
        return operationMetadata;
    }

    public void setOperationMetadata(String operationMetadata){
        this.operationMetadata = operationMetadata;
    }


}
