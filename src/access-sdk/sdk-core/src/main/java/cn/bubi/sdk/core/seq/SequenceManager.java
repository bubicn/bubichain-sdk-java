package cn.bubi.sdk.core.seq;

import cn.bubi.sdk.core.exception.SdkException;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 下午5:02.
 */
public interface SequenceManager{

    /**
     * 初始化
     */
    void init();

    /**
     * 销毁
     */
    void destroy();

    /**
     * 获取指定区块链地址的下一个可提交的交易序列号；
     */
    long getSequenceNumber(String address) throws SdkException;

    /**
     * 重置，提交失败时调用
     */
    void reset(String address);

}
