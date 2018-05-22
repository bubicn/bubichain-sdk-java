package cn.bubi.sdk.core.spi;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;
import cn.bubi.access.adaptation.blockchain.bc.response.Account;
import cn.bubi.access.adaptation.blockchain.bc.response.TransactionHistory;
import cn.bubi.access.adaptation.blockchain.bc.response.operation.SetMetadata;
import cn.bubi.access.utils.spring.StringUtils;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 下午5:15.
 */
public class BcQueryServiceImpl implements BcQueryService{

    private RpcService rpcService;

    public BcQueryServiceImpl(RpcService rpcService){
        this.rpcService = rpcService;
    }


    @Override
    public Account getAccount(String address){
        if (StringUtils.isEmpty(address)) {
            throw new IllegalArgumentException("query account method address must not null!");
        }
        return rpcService.getAccount(address);
    }

    @Override
    public SetMetadata getAccount(String address, String key){
        if (StringUtils.isEmpty(address) || StringUtils.isEmpty(key)) {
            throw new IllegalArgumentException("query account method address and key must not null!");
        }
        return rpcService.getAccountMetadata(address, key);
    }

    @Override
    public TransactionHistory getTransactionHistoryByHash(String hash){
        if (StringUtils.isEmpty(hash)) {
            throw new IllegalArgumentException("query getTransactionHistoryByHash method hash must not null!");
        }
        return rpcService.getTransactionHistoryByHash(hash);
    }

}
