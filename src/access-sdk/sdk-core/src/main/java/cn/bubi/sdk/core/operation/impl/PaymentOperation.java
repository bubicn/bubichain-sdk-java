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
 * 资产转移
 */
public class PaymentOperation extends AbstractBcOperation{

    private String targetAddress;
    private long amount;
    private String issuerAddress;
    private String assetCode;

    private PaymentOperation(){
        super(OperationTypeV3.PAYMENT.intValue());
    }

    @Override
    protected void buildOperationContinue(Chain.Operation.Builder operation){
        Chain.OperationPayment.Builder operationPayment = Chain.OperationPayment.newBuilder();
        operationPayment.setDestAddress(targetAddress);

        Chain.Asset.Builder asset = Chain.Asset.newBuilder();
        Chain.AssetProperty.Builder assetProperty = Chain.AssetProperty.newBuilder();
        assetProperty.setIssuer(issuerAddress);
        assetProperty.setCode(assetCode);
        asset.setProperty(assetProperty);
        asset.setAmount(amount);
        operationPayment.setAsset(asset);
        operation.setPayment(operationPayment);
    }

    public static class Builder extends BaseBuilder<PaymentOperation, Builder>{

        @Override
        protected PaymentOperation newOperation(){
            return new PaymentOperation();
        }

        public Builder buildTargetAddress(String targetAddress) throws SdkException{
            return buildTemplate(() -> operation.targetAddress = targetAddress);
        }

        public Builder buildAmount(long amount) throws SdkException{
            return buildTemplate(() -> operation.amount = amount);
        }

        public Builder buildIssuerAddress(String issuerAddress) throws SdkException{
            return buildTemplate(() -> operation.issuerAddress = issuerAddress);
        }

        public Builder buildAssetCode(String assetCode) throws SdkException{
            return buildTemplate(() -> operation.assetCode = assetCode);
        }

        @Override
        public void checkPass() throws SdkException{
            Assert.notEmpty(operation.targetAddress, SdkError.OPERATION_ERROR_NOT_DESC_ADDRESS);
            Assert.notEmpty(operation.issuerAddress, SdkError.OPERATION_ERROR_ISSUE_SOURCE_ADDRESS);
            Assert.notEmpty(operation.assetCode, SdkError.OPERATION_ERROR_ISSUE_CODE);
            Assert.gtZero(operation.amount, SdkError.OPERATION_ERROR_PAYMENT_AMOUNT_ZERO);
        }
    }

}
