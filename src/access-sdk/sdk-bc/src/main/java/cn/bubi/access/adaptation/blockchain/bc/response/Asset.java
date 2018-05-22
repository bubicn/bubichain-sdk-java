package cn.bubi.access.adaptation.blockchain.bc.response;

/**
 * 资产
 *
 * @author 陈志平
 */
public class Asset{
    private long amount;
    private Property property;

    public long getAmount(){
        return amount;
    }

    public void setAmount(long amount){
        this.amount = amount;
    }

    public Property getProperty(){
        return property;
    }

    public void setProperty(Property property){
        this.property = property;
    }
}
