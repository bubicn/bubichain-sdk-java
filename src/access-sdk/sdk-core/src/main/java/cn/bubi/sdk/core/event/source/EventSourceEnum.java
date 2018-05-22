package cn.bubi.sdk.core.event.source;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/12 下午4:39.
 */
public enum EventSourceEnum{

    LEDGER_SEQ_INCREASE(new LedgerSeqIncreaseEventSource()),
    TRANSACTION_NOTIFY(new TransactionNotifyEventSource()),

    ;
    private EventSource eventSource;

    EventSourceEnum(EventSource eventSource){
        this.eventSource = eventSource;
    }

    public EventSource getEventSource(){
        return eventSource;
    }
}
