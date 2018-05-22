package cn.bubi.access.adaptation.blockchain.bc.response;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 交易信息
 *
 * @author 陈志平
 */
public class Transaction{
    @JSONField(name = "close_time")
    private long closeTime;
    @JSONField(name = "error_code")
    private long errorCode;
    @JSONField(name = "ledger_seq")
    private long ledgerSeq;
    private Signature[] signatures;
    private SubTransaction transaction;

    private String hash;

    public long getCloseTime(){
        return closeTime;
    }

    public void setCloseTime(long closeTime){
        this.closeTime = closeTime;
    }

    public long getErrorCode(){
        return errorCode;
    }

    public void setErrorCode(long errorCode){
        this.errorCode = errorCode;
    }

    public long getLedgerSeq(){
        return ledgerSeq;
    }

    public void setLedgerSeq(long ledgerSeq){
        this.ledgerSeq = ledgerSeq;
    }

    public Signature[] getSignatures(){
        return signatures;
    }

    public void setSignatures(Signature[] signatures){
        this.signatures = signatures;
    }

    public SubTransaction getTransaction(){
        return transaction;
    }

    public void setTransaction(SubTransaction transaction){
        this.transaction = transaction;
    }

    public String getHash(){
        return hash;
    }

    public void setHash(String hash){
        this.hash = hash;
    }


}