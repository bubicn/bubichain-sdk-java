package cn.bubi.sdk.core.event.handle;

import cn.bubi.sdk.core.event.message.EventMessage;
import cn.bubi.sdk.core.event.source.EventSource;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/12 下午4:34.
 */
public abstract class AbstractEventHandler<T extends EventMessage> implements EventHandler{

    private EventSource eventSource;
    private Class<T> messageClass;

    public AbstractEventHandler(EventSource eventSource, Class<T> messageClass){
        this.eventSource = eventSource;
        this.messageClass = messageClass;
    }

    @Override
    public EventSource eventSource(){
        return eventSource;
    }

    @Override
    public void onEvent(EventMessage message){
        processMessage(messageClass.cast(message));
    }

    public abstract void processMessage(T message);

    public EventSource getEventSource(){
        return eventSource;
    }
}
