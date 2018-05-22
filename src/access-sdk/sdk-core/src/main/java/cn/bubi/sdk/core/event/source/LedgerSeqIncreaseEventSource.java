package cn.bubi.sdk.core.event.source;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 上午10:14.
 */
public class LedgerSeqIncreaseEventSource implements EventSource{

    public static final String CODE = "LEDGER_SEQ_INCREASE_EVENT_SOURCE";

    @Override
    public String getCode(){
        return CODE;
    }

    @Override
    public String getName(){
        return "区块seq增加事件";
    }

}
