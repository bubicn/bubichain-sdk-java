package cn.bubi.access.adaptation.blockchain.bc.response;

/**
 * Txset
 *
 * @author 陈志平
 */
public class Txset{
    private Transaction[] txs;

    public Transaction[] getTxs(){
        return txs;
    }

    public void setTxs(Transaction[] txs){
        this.txs = txs;
    }

}
