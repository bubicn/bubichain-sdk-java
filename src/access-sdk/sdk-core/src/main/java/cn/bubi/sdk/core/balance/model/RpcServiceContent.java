package cn.bubi.sdk.core.balance.model;

import cn.bubi.access.adaptation.blockchain.bc.RpcService;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/26 下午5:33.
 */
public class RpcServiceContent{

    private String host;
    private RpcService rpcService;

    public RpcServiceContent(String host, RpcService rpcService){
        this.host = host;
        this.rpcService = rpcService;
    }

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public RpcService getRpcService(){
        return rpcService;
    }

    public void setRpcService(RpcService rpcService){
        this.rpcService = rpcService;
    }
}
