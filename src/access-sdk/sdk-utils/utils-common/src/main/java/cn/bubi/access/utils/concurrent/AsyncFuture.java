package cn.bubi.access.utils.concurrent;

import java.util.concurrent.TimeoutException;

/**
 * 提供对异步传输操作的结果描述；
 *
 * @param <TSource>
 * @author haiq
 */
public interface AsyncFuture<TSource>{

    /**
     * 返回执行异步操作的对象；
     *
     * @return
     */
    public TSource getSource();

    /**
     * 操作是否已完成；
     * <p>
     * 当操作成功返回或者异常返回时，都表示为已完成；
     *
     * @return
     */
    public boolean isDone();

    /**
     * 操作是否已成功；
     *
     * @return
     */
    public boolean isSuccess();

    public String getErrorCode();

    String getErrorMessage();

    /**
     * 返回操作异常；
     * <p>
     * 当未完成(isDone方法返回false)或操作正常结束时，返回 null；
     *
     * @return
     */
    public Throwable getException();

    /**
     * 等待异步操作完成后返回；
     *
     * @throws InterruptedException
     */
    public void await() throws InterruptedException, TimeoutException;

    /**
     * 等待异步操作完成后返回；
     *
     * @param timeoutMillis 超时毫秒数；
     * @return true 表示操作已完成； false 表示超时返回；
     * @throws InterruptedException
     */
    public boolean await(long timeoutMillis) throws InterruptedException, TimeoutException;

    /**
     * 等待异步操作完成后返回；
     * <p>
     * 等待过程不触发中断；
     */
    public void awaitUninterruptibly();

    /**
     * 等待异步操作完成后返回；
     * <p>
     * 等待过程不触发中断；
     *
     * @param timeoutMillis 超时毫秒数；
     * @return true 表示操作已完成； false 表示超时返回；
     */
    public boolean awaitUninterruptibly(long timeoutMillis);

    /**
     * 注册监听器；
     *
     * @param listener
     */
    public void addListener(AsyncFutureListener<TSource> listener);

}
