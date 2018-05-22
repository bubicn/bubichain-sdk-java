package cn.bubi.sdk.core.spi;

import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.adaptation.blockchain.bc.response.TransactionHistory;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:38.
 * 区块链查询服务
 */
public interface BcQueryService{

    /**
     * 获得账户信息
     *
     * @param address 账户地址
     */
    Account getAccount(String address);

    /**
     * 获得指定账户metadata的value
     *
     * @param address 账户地址
     * @param key     metadata的key
     */
    SetMetadata getAccount(String address, String key);

    /**
     * 获得交易历史
     *
     * @param hash txHash
     */
    TransactionHistory getTransactionHistoryByHash(String hash);

}
