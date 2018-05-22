package cn.bubi.sdk.core.event;

import cn.bubi.sdk.core.event.handle.EventHandler;
import cn.bubi.sdk.core.event.message.EventMessage;
import cn.bubi.sdk.core.event.source.EventSource;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/12 下午4:44.
 */
public interface EventBusService{

    void clear();

    void addEventHandler(EventHandler eventHandle);

    void publishEvent(EventSource eventSource, EventMessage eventMessage);

}
