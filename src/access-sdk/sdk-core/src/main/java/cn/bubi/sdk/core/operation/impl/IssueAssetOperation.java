package cn.bubi.sdk.core.operation.impl;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.blockchain.adapter3.Chain;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.AbstractBcOperation;
import cn.bubi.sdk.core.operation.builder.BaseBuilder;
import cn.bubi.sdk.core.utils.Assert;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:56.
 */
public class IssueAssetOperation extends AbstractBcOperation{

    private long amount;
    private String assetCode;

    private IssueAssetOperation(){
        super(OperationTypeV3.ISSUE_ASSET.intValue());
    }

    @Override
    protected void buildOperationContinue(Chain.Operation.Builder operation){

        Chain.OperationIssueAsset.Builder operationIssueAsset = Chain.OperationIssueAsset.newBuilder();

        Chain.Asset.Builder asset = Chain.Asset.newBuilder();
        Chain.AssetProperty.Builder assetProperty = Chain.AssetProperty.newBuilder();
        assetProperty.setCode(assetCode);

        asset.setProperty(assetProperty);
        asset.setAmount(amount);
        operationIssueAsset.setCode(assetCode);
        operationIssueAsset.setAmount(amount);

        operation.setIssueAsset(operationIssueAsset);
    }


    public static class Builder extends BaseBuilder<IssueAssetOperation, Builder>{
        @Override
        protected IssueAssetOperation newOperation(){
            return new IssueAssetOperation();
        }

        public Builder buildAmount(long amount) throws SdkException{
            return buildTemplate(() -> operation.amount = amount);
        }

        public Builder buildAssetCode(String assetCode) throws SdkException{
            return buildTemplate(() -> operation.assetCode = assetCode);
        }

        @Override
        public void checkPass() throws SdkException{
            Assert.notEmpty(operation.assetCode, SdkError.OPERATION_ERROR_ISSUE_CODE);
            Assert.gteZero(operation.amount, SdkError.OPERATION_ERROR_ISSUE_AMOUNT_ZERO);
        }

    }

}
