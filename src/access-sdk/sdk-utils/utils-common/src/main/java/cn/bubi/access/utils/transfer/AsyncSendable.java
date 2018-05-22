package cn.bubi.access.utils.transfer;


import cn.bubi.access.utils.concurrent.AsyncFuture;

/**
 * AsyncMessageSendable 是对异步发送操作的抽象；
 *
 * @param <TData>
 * @author haiq
 */
public interface AsyncSendable<TSender, TData>{

    /**
     * 异步发送消息；
     *
     * @param message
     * @return
     */
    public AsyncFuture<TSender> asyncSend(TData message);

}
