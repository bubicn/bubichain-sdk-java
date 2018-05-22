package cn.bubi.sdk.core.seq;

import cn.bubi.sdk.core.event.handle.AbstractEventHandler;
import cn.bubi.sdk.core.event.message.TransactionExecutedEventMessage;
import cn.bubi.sdk.core.event.source.EventSource;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/14 下午6:27.
 */
public abstract class AbstractSequenceManager extends AbstractEventHandler<TransactionExecutedEventMessage> implements SequenceManager{

    public AbstractSequenceManager(EventSource eventSource, Class<TransactionExecutedEventMessage> messageClass){
        super(eventSource, messageClass);
    }

    @Override
    public void init(){
    }

    @Override
    public void destroy(){
    }

}
