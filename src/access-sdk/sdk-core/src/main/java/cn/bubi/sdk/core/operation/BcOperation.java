package cn.bubi.sdk.core.operation;

import cn.bubi.blockchain.adapter3.Chain;
import cn.bubi.sdk.core.exception.SdkException;
import com.alibaba.fastjson.JSONObject;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:45.
 */
public interface BcOperation{

    /**
     * 整合操作
     */
    void buildTransaction(Chain.Transaction.Builder builder, long lastSeq) throws SdkException;

    /**
     * 反序列化操作
     */
    BcOperation generateOperation(JSONObject originJson);


}
