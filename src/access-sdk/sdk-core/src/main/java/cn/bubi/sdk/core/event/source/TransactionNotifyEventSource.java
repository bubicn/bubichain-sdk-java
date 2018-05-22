package cn.bubi.sdk.core.event.source;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 上午10:14.
 */
public class TransactionNotifyEventSource implements EventSource{

    public static final String CODE = "TRANSACTION_NOTIFY_EVENT_SOURCE";

    @Override
    public String getCode(){
        return CODE;
    }

    @Override
    public String getName(){
        return "交易通知事件源";
    }

}
