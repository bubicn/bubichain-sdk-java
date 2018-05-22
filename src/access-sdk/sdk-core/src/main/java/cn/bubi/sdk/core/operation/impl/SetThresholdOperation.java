package cn.bubi.sdk.core.operation.impl;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.TypeThreshold;
import cn.bubi.blockchain.adapter3.Chain;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.AbstractBcOperation;
import cn.bubi.sdk.core.operation.builder.BaseBuilder;
import cn.bubi.sdk.core.utils.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:56.
 */
public class SetThresholdOperation extends AbstractBcOperation{

    private static final long UNMODIFIED = -1;// 如果不想修改需要设置为-1

    private long txThreshold = UNMODIFIED;
    private List<TypeThreshold> typeThresholds = new ArrayList<>();// 0删除

    private SetThresholdOperation(){
        super(OperationTypeV3.SET_THRESHOLD.intValue());
    }

    @Override
    protected void buildOperationContinue(Chain.Operation.Builder operation){
        Chain.OperationSetThreshold.Builder operationSetThreshold = Chain.OperationSetThreshold.newBuilder();
        operationSetThreshold.setTxThreshold(txThreshold);

        typeThresholds.forEach(typeThreshold -> {
            Chain.OperationTypeThreshold.Builder typeThresholdBuilder = Chain.OperationTypeThreshold.newBuilder();
            typeThresholdBuilder
                    .setType(Chain.Operation.Type.forNumber(Integer.valueOf("" + typeThreshold.getType())));
            typeThresholdBuilder.setThreshold(typeThreshold.getThreshold());
            operationSetThreshold.addTypeThresholds(typeThresholdBuilder);
        });

        operation.setSetThreshold(operationSetThreshold);
    }


    public static class Builder extends BaseBuilder<SetThresholdOperation, Builder>{

        @Override
        protected SetThresholdOperation newOperation(){
            return new SetThresholdOperation();
        }


        public Builder buildTxThreshold(long txThreshold) throws SdkException{
            return buildTemplate(() -> operation.txThreshold = txThreshold);
        }

        public Builder buildAddTypeThreshold(OperationTypeV3 type, long threshold) throws SdkException{
            return buildTemplate(() -> {
                Assert.notNull(type, SdkError.OPERATION_ERROR_TX_THRESHOLD_TYPE_NOT_NULL);
                operation.typeThresholds.add(new TypeThreshold(type.intValue(), threshold));
            });
        }

        @Override
        public void checkPass() throws SdkException{
            Assert.notTrue(operation.txThreshold == UNMODIFIED && operation.typeThresholds.isEmpty(), SdkError.OPERATION_ERROR_SET_THRESHOLD);
            Assert.gteExpect(operation.txThreshold, UNMODIFIED, SdkError.OPERATION_ERROR_TX_THRESHOLD_LT_ZERO);
            Assert.checkCollection(operation.typeThresholds, typeThreshold -> Assert.gteZero(typeThreshold.getThreshold(), SdkError.OPERATION_ERROR_TX_THRESHOLD_TYPE_LT_ZERO));
        }

    }

}
