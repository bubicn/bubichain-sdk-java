package cn.bubi.sdk.core.event.handle;

import cn.bubi.sdk.core.event.message.EventMessage;
import cn.bubi.sdk.core.event.source.EventSource;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 上午10:17.
 * 事件处理器
 */
public interface EventHandler{

    /**
     * 事件源
     */
    EventSource eventSource();

    /**
     * 事件处理器
     */
    void onEvent(EventMessage message);

}
