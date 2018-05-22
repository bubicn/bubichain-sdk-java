package cn.bubi.sdk.core.operation;

import cn.bubi.access.adaptation.blockchain.bc.OperationTypeV3;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.impl.*;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:41.
 * 提供参数化创建操作方法，这里只是便携访问，所有的操作均可自行创建，上层可自行封装便捷调用
 */
public class OperationFactory{

    /**
     * 创建调用合约操作
     *
     * @param destAddress 目的地址
     * @param inputData   合约参数
     */
    public static InvokeContractOperation newInvokeContractOperation(String destAddress, String inputData) throws SdkException{
        return new InvokeContractOperation.Builder().buildDestAddress(destAddress).buildInputData(inputData).build();
    }

    /**
     * 发行资产
     *
     * @param assetCode 资产编码
     * @param amount    资产数量
     */
    public static IssueAssetOperation newIssueAssetOperation(String assetCode, long amount) throws SdkException{
        return new IssueAssetOperation.Builder().buildAmount(amount).buildAssetCode(assetCode).build();
    }

    /**
     * 资产转移
     *
     * @param targetAddress 目标地址
     * @param issuerAddress 源地址
     * @param assetCode     资产编码
     * @param amount        资产数量
     */
    public static PaymentOperation newPaymentOperation(String targetAddress, String issuerAddress, String assetCode, long amount) throws SdkException{
        return new PaymentOperation.Builder().buildAmount(amount).buildAssetCode(assetCode).buildTargetAddress(targetAddress).buildIssuerAddress(issuerAddress).build();
    }

    /**
     * 增加metadata,只能增加，不能修改
     *
     * @param key   metadata-key
     * @param value metadata-value
     */
    public static SetMetadataOperation newSetMetadataOperation(String key, String value) throws SdkException{
        return new SetMetadataOperation.Builder().buildMetadata(key, value).build();
    }

    /**
     * metadata操作，自己处理version，注意在提交的时候会将这里设置的version+1
     *
     * @param key            metadata-key
     * @param value          metadata-value
     * @param currentVersion 当前查询出的version,无0
     */
    public static SetMetadataOperation newSetMetadataOperation(String key, String value, long currentVersion) throws SdkException{
        return new SetMetadataOperation.Builder().buildMetadata(key, value, currentVersion).build();
    }

    /**
     * 修改metadata
     * 修改必须先查询，否则修改会失败
     *
     * @param setMetadats metadata列表
     */
    public static SetMetadataOperation newUpdateSetMetadataOperation(SetMetadata setMetadats) throws SdkException{
        return new SetMetadataOperation.Builder().buildMetadata(setMetadats).build();
    }

    /**
     * 设置/更新权重
     *
     * @param masterWeight 权重
     */
    public static SetSignerWeightOperation newSetSignerWeightOperation(long masterWeight) throws SdkException{
        return new SetSignerWeightOperation.Builder().buildMasterWeight(masterWeight).build();
    }

    /**
     * 设置/更新签名列表
     *
     * @param address 签名地址
     * @param weight  权重
     */
    public static SetSignerWeightOperation newSetSignerWeightOperation(String address, long weight) throws SdkException{
        return new SetSignerWeightOperation.Builder().buildAddSigner(address, weight).build();
    }

    /**
     * 设置/更新交易门限
     *
     * @param txThreshold 交易门限
     */
    public static SetThresholdOperation newSetThresholdOperation(long txThreshold) throws SdkException{
        return new SetThresholdOperation.Builder().buildTxThreshold(txThreshold).build();
    }

    /**
     * 设置/更新操作门限
     *
     * @param operationTypeV3 操作类型
     * @param threshold       具体门限
     */
    public static SetThresholdOperation newSetThresholdOperation(OperationTypeV3 operationTypeV3, long threshold) throws SdkException{
        return new SetThresholdOperation.Builder().buildAddTypeThreshold(operationTypeV3, threshold).build();
    }


}
