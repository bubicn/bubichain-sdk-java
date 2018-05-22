package cn.bubi.access.utils.concurrent;

public interface AsyncFutureListener<TSource>{

    public void complete(AsyncFuture<TSource> future);

}
