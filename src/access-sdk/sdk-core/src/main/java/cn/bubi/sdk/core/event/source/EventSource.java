package cn.bubi.sdk.core.event.source;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/25 上午10:12.
 * 简单的事件定义
 */
public interface EventSource{

    /**
     * 事件代码
     */
    String getCode();

    /**
     * 事件名
     */
    String getName();

}
