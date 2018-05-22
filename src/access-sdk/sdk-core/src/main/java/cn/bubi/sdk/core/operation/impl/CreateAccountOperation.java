package cn.bubi.sdk.core.operation.impl;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.Priv;
import cn.bubi.access.adaptation.blockchain.bc.response.Signer;
import cn.bubi.access.adaptation.blockchain.bc.response.Threshold;
import cn.bubi.access.adaptation.blockchain.bc.response.TypeThreshold;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.CreateAccount;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;
import cn.bubi.access.utils.spring.StringUtils;
import cn.bubi.blockchain.adapter3.Chain;
import cn.bubi.blockchain.adapter3.Common;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.AbstractBcOperation;
import cn.bubi.sdk.core.operation.builder.BaseBuilder;
import cn.bubi.sdk.core.utils.Assert;

import java.util.List;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:45.
 */
public class CreateAccountOperation extends AbstractBcOperation{

    private CreateAccount createAccount = new CreateAccount();

    private CreateAccountOperation(){
        super(OperationTypeV3.CREATE_ACCOUNT.intValue());
    }

    @Override
    protected void buildOperationContinue(Chain.Operation.Builder operation){
        Chain.OperationCreateAccount.Builder operationCreateAccount = Chain.OperationCreateAccount.newBuilder();
        operationCreateAccount.setDestAddress(createAccount.getDestAddress());

        // Meta Data
        List<SetMetadata> metadatas = createAccount.getMetadatas();
        if (metadatas != null && metadatas.size() > 0) {
            metadatas.forEach(m -> {
                Common.KeyPair.Builder keyPair = Common.KeyPair.newBuilder();
                keyPair.setKey(m.getKey());
                keyPair.setValue(m.getValue());
                keyPair.setVersion(m.getVersion());
                operationCreateAccount.addMetadatas(keyPair);
            });
        }

        // 权限
        Priv priv = createAccount.getPriv();
        Chain.AccountPrivilege.Builder accountPrivilegeBuilder = Chain.AccountPrivilege.newBuilder();
        accountPrivilegeBuilder.setMasterWeight(priv.getMasterWeight());
        // signer
        if (priv.getSigners() != null) {
            priv.getSigners().forEach(privileger -> {
                Chain.Signer.Builder sign = Chain.Signer.newBuilder();
                sign.setAddress(privileger.getAddress());
                sign.setWeight(privileger.getWeight());
                accountPrivilegeBuilder.addSigners(sign);
            });
        }
        // threshold
        Threshold threshold = priv.getThreshold();
        if (threshold != null) {
            Chain.AccountThreshold.Builder accountThresholdBuilder = Chain.AccountThreshold.newBuilder();
            accountThresholdBuilder.setTxThreshold(threshold.getTxThreshold());

            // type_thresholds
            Chain.OperationTypeThreshold.Builder typeThresholdBuilder = Chain.OperationTypeThreshold.newBuilder();
            List<TypeThreshold> typeThresholds = threshold.getTypeThresholds();
            if (typeThresholds != null && typeThresholds.size() > 0) {
                typeThresholds.forEach(typeThreshold -> {
                    typeThresholdBuilder.setType(Chain.Operation.Type.forNumber(Integer.valueOf("" + typeThreshold.getType())));
                    typeThresholdBuilder.setThreshold(typeThreshold.getThreshold());
                    accountThresholdBuilder.addTypeThresholds(typeThresholdBuilder);
                });
            }

            accountPrivilegeBuilder.setThresholds(accountThresholdBuilder);
        }

        operationCreateAccount.setPriv(accountPrivilegeBuilder);

        // Contract 合约
        String script = createAccount.getContract().getPayload();
        if (!StringUtils.isEmpty(script)) {
            Chain.Contract.Builder contractBuilder = Chain.Contract.newBuilder();
            contractBuilder.setPayload(script);
            operationCreateAccount.setContract(contractBuilder);
        }
        operation.setCreateAccount(operationCreateAccount);
    }


    public static class Builder extends BaseBuilder<CreateAccountOperation, Builder>{

        private CreateAccount createAccount;

        @Override
        protected CreateAccountOperation newOperation(){
            CreateAccountOperation createAccountOperation = new CreateAccountOperation();
            this.createAccount = createAccountOperation.createAccount;
            return createAccountOperation;
        }

        public Builder buildDestAddress(String destAddress) throws SdkException{
            return buildTemplate(() -> createAccount.setDestAddress(destAddress));
        }

        public Builder buildScript(String script) throws SdkException{
            return buildTemplate(() -> createAccount.getContract().setPayload(script));
        }

        public Builder buildAddMetadata(String key, String value) throws SdkException{
            return buildTemplate(() -> createAccount.getMetadatas().add(new SetMetadata(key, value)));
        }

        public Builder buildPriMasterWeight(long masterWeight) throws SdkException{
            return buildTemplate(() -> createAccount.getPriv().setMasterWeight(masterWeight));
        }

        public Builder buildAddPriSigner(String address, long weight) throws SdkException{
            return buildTemplate(() -> createAccount.getPriv().getSigners().add(new Signer(address, weight)));
        }

        public Builder buildPriTxThreshold(long txThreshold) throws SdkException{
            return buildTemplate(() -> createAccount.getPriv().getThreshold().setTxThreshold(txThreshold));
        }

        public Builder buildAddPriTypeThreshold(OperationTypeV3 operationTypeV3, long threshold) throws SdkException{
            return buildTemplate(() -> createAccount.getPriv().getThreshold().getTypeThresholds().add(new TypeThreshold(operationTypeV3.intValue(), threshold)));
        }

        @Override
        public void checkPass() throws SdkException{
            Assert.notEmpty(operation.createAccount.getDestAddress(), SdkError.OPERATION_ERROR_NOT_DESC_ADDRESS);
        }
    }

}
