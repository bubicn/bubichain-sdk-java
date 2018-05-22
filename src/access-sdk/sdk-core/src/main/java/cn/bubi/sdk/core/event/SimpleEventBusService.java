package cn.bubi.sdk.core.event;

import cn.bubi.sdk.core.event.handle.EventHandler;
import cn.bubi.sdk.core.event.message.EventMessage;
import cn.bubi.sdk.core.event.source.EventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 上午10:15.
 * 简单的事件通知总线
 */
public class SimpleEventBusService implements EventBusService{

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventBusService.class);

    private static final Map<String, List<EventHandler>> EVENT_HANDLE_MAP = new ConcurrentHashMap<>();

    private static final ExecutorService EVENT_EXECUTOR = new
            ThreadPoolExecutor(5, 200, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(1000));

    @Override
    public void clear(){
        EVENT_HANDLE_MAP.clear();
    }

    @Override
    public synchronized void addEventHandler(EventHandler eventHandle){
        List<EventHandler> eventHandlers = EVENT_HANDLE_MAP.computeIfAbsent(eventHandle.eventSource().getCode(), k -> new ArrayList<>());
        eventHandlers.add(eventHandle);
    }

    @Override
    public void publishEvent(EventSource eventSource, EventMessage eventMessage){
        String eventCode = eventSource.getCode();
        List<EventHandler> eventHandlers = EVENT_HANDLE_MAP.get(eventCode);
        if (eventHandlers == null || eventHandlers.isEmpty()) {
            LOGGER.debug("not found event handle , event code:" + eventCode);
            return;
        }

        eventHandlers.forEach(eventHandler -> EVENT_EXECUTOR.execute(() -> eventHandler.onEvent(eventMessage)));
    }

}
