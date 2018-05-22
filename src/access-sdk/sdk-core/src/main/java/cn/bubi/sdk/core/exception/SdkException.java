package cn.bubi.sdk.core.exception;

import cn.bubi.access.adaptation.blockchain.exception.BlockchainError;
import cn.bubi.access.adaptation.blockchain.exception.BlockchainException;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 下午3:05.
 * SdkException可预期异常
 */
public class SdkException extends Exception{

    private static final long serialVersionUID = -1934407327912767401L;

    private int errorCode;    //错误详细信息
    private String errorMessage;    //错误详细信息

    public SdkException(){
        super();
    }

    public SdkException(int code, String message){
        super(message);
        this.errorCode = code;
        this.errorMessage = message;
    }

    public SdkException(BlockchainException be){
        super(be);
        this.errorCode = be.getErrorCode();
        this.errorMessage = be.getErrorMessage();
    }

    public SdkException(BlockchainError blockchainError){
        super(blockchainError.getDescription());
        this.errorCode = blockchainError.getCode();
        this.errorMessage = blockchainError.getDescription();
    }

    public SdkException(SdkError sdkError){
        super(sdkError.getDescription());
        this.errorCode = sdkError.getCode();
        this.errorMessage = sdkError.getDescription();
    }

    public SdkException(int code, String message, Throwable throwable){
        super(message, throwable);
        this.errorCode = code;
        this.errorMessage = message;
    }

    public int getErrorCode(){
        return errorCode;
    }

    public void setErrorCode(int errorCode){
        this.errorCode = errorCode;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString(){
        return "SdkException{" +
                "errorCode=" + errorCode +
                ", errorMessage='" + errorMessage + '\'' +
                "} " + super.toString();
    }
}
