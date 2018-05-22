package cn.bubi.sdk.core.event.message;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/26 下午4:15.
 */
public class LedgerSeqEventMessage implements EventMessage{

    private String host;// 节点host

    private long seq;// 当前seq


    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public long getSeq(){
        return seq;
    }

    public void setSeq(long seq){
        this.seq = seq;
    }
}
