package cn.bubi.sdk.core.operation.impl;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;
import cn.bubi.blockchain.adapter3.Chain;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.AbstractBcOperation;
import cn.bubi.sdk.core.operation.builder.BaseBuilder;
import cn.bubi.sdk.core.utils.Assert;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:56.
 * 一次metadata更新只能更新一个
 */
public class SetMetadataOperation extends AbstractBcOperation{

    private SetMetadata setMetadata;

    private SetMetadataOperation(){
        super(OperationTypeV3.SET_METADATA.intValue());
    }

    @Override
    protected void buildOperationContinue(Chain.Operation.Builder operation){
        Chain.OperationSetMetadata.Builder operationSetMetadata = Chain.OperationSetMetadata.newBuilder();
        operationSetMetadata.setKey(setMetadata.getKey());
        operationSetMetadata.setValue(setMetadata.getValue());
        if (setMetadata.getVersion() != 0) {
            operationSetMetadata.setVersion(setMetadata.getVersion() + 1);
        }
        operation.setSetMetadata(operationSetMetadata);
    }

    public static class Builder extends BaseBuilder<SetMetadataOperation, Builder>{

        @Override
        protected SetMetadataOperation newOperation(){
            return new SetMetadataOperation();
        }

        public Builder buildMetadata(SetMetadata setMetadata) throws SdkException{
            return buildTemplate(() -> operation.setMetadata = setMetadata);
        }

        public Builder buildMetadata(String key, String value) throws SdkException{
            return buildTemplate(() -> operation.setMetadata = new SetMetadata(key, value));
        }

        public Builder buildMetadata(String key, String value, long version) throws SdkException{
            return buildTemplate(() -> operation.setMetadata = new SetMetadata(key, value, version));
        }

        @Override
        public void checkPass() throws SdkException{
            Assert.notTrue(operation.setMetadata == null, SdkError.OPERATION_ERROR_SET_METADATA_EMPTY);
        }

    }

}
