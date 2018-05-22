package cn.bubi.sdk.core.transaction.model;

import java.io.Serializable;

/**
 * 事务提交结果；
 */
public class TransactionCommittedResult implements Serializable{

    /**
     * 交易hash；
     */
    private String hash;


    public String getHash(){
        return hash;
    }

    public void setHash(String hash){
        this.hash = hash;
    }

}
