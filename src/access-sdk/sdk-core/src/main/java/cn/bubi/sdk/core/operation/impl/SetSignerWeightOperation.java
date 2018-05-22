package cn.bubi.sdk.core.operation.impl;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.common.Signer;
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
public class SetSignerWeightOperation extends AbstractBcOperation{

    private static final long UNMODIFIED = -1;// 如果不想修改需要设置为-1

    private long masterWeight = UNMODIFIED;
    private List<Signer> signers = new ArrayList<>();// 0删除

    private SetSignerWeightOperation(){
        super(OperationTypeV3.SET_SIGNER_WEIGHT.intValue());
    }

    @Override
    protected void buildOperationContinue(Chain.Operation.Builder operation){
        Chain.OperationSetSignerWeight.Builder operationSetSignerWeight = Chain.OperationSetSignerWeight.newBuilder();
        operationSetSignerWeight.setMasterWeight(masterWeight);
        signers.forEach(signer -> {
            Chain.Signer.Builder sign = Chain.Signer.newBuilder();
            sign.setAddress(signer.getAddress());
            sign.setWeight(signer.getWeight());
            operationSetSignerWeight.addSigners(sign);
        });

        operation.setSetSignerWeight(operationSetSignerWeight);
    }


    public static class Builder extends BaseBuilder<SetSignerWeightOperation, Builder>{

        @Override
        protected SetSignerWeightOperation newOperation(){
            return new SetSignerWeightOperation();
        }

        public Builder buildMasterWeight(long masterWeight) throws SdkException{
            return buildTemplate(() -> operation.masterWeight = masterWeight);
        }

        public Builder buildAddSigner(String address, long weight) throws SdkException{
            return buildTemplate(() -> operation.signers.add(new Signer(address, weight)));
        }

        @Override
        public void checkPass() throws SdkException{
            Assert.notTrue(operation.masterWeight == UNMODIFIED && operation.signers.isEmpty(), SdkError.OPERATION_ERROR_SET_SIGNER_WEIGHT);
            Assert.gteExpect(operation.masterWeight, UNMODIFIED, SdkError.OPERATION_ERROR_MASTER_WEIGHT_LT_ZERO);
            Assert.checkCollection(operation.signers, signer -> {
                Assert.notEmpty(signer.getAddress(), SdkError.OPERATION_ERROR_SET_SIGNER_ADDRESS_NOT_EMPTY);
                Assert.gteZero(signer.getWeight(), SdkError.OPERATION_ERROR_SINGER_WEIGHT_LT_ZERO);
            });
        }

    }

}
