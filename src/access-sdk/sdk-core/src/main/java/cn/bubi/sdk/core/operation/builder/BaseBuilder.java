package cn.bubi.sdk.core.operation.builder;

import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;
import cn.bubi.sdk.core.operation.AbstractBcOperation;
import cn.bubi.sdk.core.operation.BuildConsume;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/6 下午3:25.
 * 将操作的创建部分移到builder中进行
 */
public abstract class BaseBuilder<T extends AbstractBcOperation, R extends BaseBuilder>{

    protected T operation;
    private String operationSourceAddress;
    private String operationMetadata;
    private boolean complete = false;

    protected BaseBuilder(){
        this.operation = newOperation();
    }

    /**
     * 生成实际操作对象
     */
    protected abstract T newOperation();

    @SuppressWarnings("unchecked")
    protected R buildTemplate(BuildConsume buildConsume) throws SdkException{
        checkOperationCanExecute();
        buildConsume.build();
        return (R) this;
    }

    private void checkOperationCanExecute() throws SdkException{
        if (complete) throw new SdkException(SdkError.OPERATION_ERROR_STATUS);
    }

    /**
     * 构造操作sourceAddress
     */
    public R buildOperationSourceAddress(String operationSourceAddress) throws SdkException{
        return buildTemplate(() -> this.operationSourceAddress = operationSourceAddress);
    }

    /**
     * 构造操作metadata
     */
    public R buildOperationMetadata(String operationMetadata) throws SdkException{
        return buildTemplate(() -> this.operationMetadata = operationMetadata);
    }

    /**
     * 生成操作对象
     */
    public T build() throws SdkException{
        complete();
        operation.setOperationSourceAddress(operationSourceAddress);
        operation.setOperationMetadata(operationMetadata);
        return operation;
    }

    private void complete() throws SdkException{
        if (!complete) {
            checkPass();
            complete = true;
        }
    }

    /**
     * 操作参数检查
     */
    public abstract void checkPass() throws SdkException;

}
