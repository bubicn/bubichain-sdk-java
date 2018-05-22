package cn.bubi.sdk.core.operation;

import cn.bubi.sdk.core.exception.SdkException;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 上午9:55.
 */
@FunctionalInterface
public interface BuildConsume{

    void build() throws SdkException;

}