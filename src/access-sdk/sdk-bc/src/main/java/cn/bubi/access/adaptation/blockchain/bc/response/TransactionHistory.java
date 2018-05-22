package cn.bubi.access.adaptation.blockchain.bc.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 交易历史返回体
 *
 * @author hobo
 */
public class TransactionHistory{
    @JSONField(name = "total_count")
    private long totalCount;
    private Transaction[] transactions;

    public long getTotalCount(){
        return totalCount;
    }

    public void setTotalCount(long totalCount){
        this.totalCount = totalCount;
    }

    public Transaction[] getTransactions(){
        return transactions;
    }

    public void setTransactions(Transaction[] transactions){
        this.transactions = transactions;
    }
}
