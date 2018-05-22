package cn.bubi.sdk.core.utils;

import cn.bubi.access.utils.spring.StringUtils;
import cn.bubi.sdk.core.exception.SdkError;
import cn.bubi.sdk.core.exception.SdkException;

import java.util.Collection;
import java.util.List;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 下午3:53.
 */
public class Assert{

    public static void isTrue(boolean expr, SdkError sdkError) throws SdkException{
        if (!expr) throw new SdkException(sdkError);
    }

    public static void notTrue(boolean expr, SdkError sdkError) throws SdkException{
        if (expr) throw new SdkException(sdkError);
    }

    public static void isNull(Object object, SdkError sdkError) throws SdkException{
        if (object != null) throw new SdkException(sdkError);
    }

    public static void notNull(Object object, SdkError sdkError) throws SdkException{
        if (object == null) throw new SdkException(sdkError);
    }

    public static void notEmpty(String str, SdkError sdkError) throws SdkException{
        if (StringUtils.isEmpty(str)) throw new SdkException(sdkError);
    }

    public static void notEmpty(List<?> list, SdkError sdkError) throws SdkException{
        if (list == null || list.isEmpty()) throw new SdkException(sdkError);
    }

    public static void gteZero(long balance, SdkError sdkError) throws SdkException{
        if (balance < 0) throw new SdkException(sdkError);
    }

    public static void gtZero(long balance, SdkError sdkError) throws SdkException{
        if (balance <= 0) throw new SdkException(sdkError);
    }

    public static void gteExpect(long actual, long expect, SdkError sdkError) throws SdkException{
        if (actual < expect) throw new SdkException(sdkError);
    }

    public static <T> void checkCollection(Collection<T> collection, SDKConsumer<T> consumer) throws SdkException{
        for (T t : collection) {
            consumer.accept(t);
        }
    }


    @FunctionalInterface
    public interface SDKConsumer<T>{

        void accept(T t) throws SdkException;

    }

}
