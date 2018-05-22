package cn.bubi.baas.utils.http;

import cn.bubi.baas.utils.http.converters.ObjectToStringConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于标识一个方法参数或一个类的成员该如何体现为 http 请求中的一个参数；<p>
 * <p>
 * 如果采用 HTTP GET 方法发起请求，则 RequestParam 标识的参数将以查询参数的方式通过 URL 提交；<p>
 * <p>
 * 如果采用 HTTP POST 方法发起请求，则 RequestParam 标识的参数将以表单字段的方式提交；<p>
 *
 * @author haiq
 */
@Target({ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam{

    /**
     * 参数名；
     *
     * @return
     */
    public String name();

    /**
     * 参数是否必须提供； <p>
     * <p>
     * 如果必须的参数的值为 null ，将引发异常；<p>
     * <p>
     * 如果非必须的参数的值为 null，则忽略此参数；<p>
     * <p>
     * 默认为 true；
     *
     * @return
     */
    public boolean required() default true;

    /**
     * 忽略值；
     * <p>
     * 仅当 require 为 false 时有效，指示当参数的值为 null 或者与 ignoreValue 相等时将忽略参数；
     *
     * @return
     */
    public String ignoreValue() default "";

    /**
     * 参数值转换器的类型；
     * <p>
     * 指定的参数值转换器必须实现 StringConverter 接口；
     * <p>
     * 如果未指定，则默认通过 toString() 方法获取参数最终的文本值；
     *
     * @return
     */
    public Class<?> converter() default ObjectToStringConverter.class;
}
