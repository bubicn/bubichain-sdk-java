package cn.bubi.baas.utils.http.agent;

import cn.bubi.baas.utils.http.HttpMethod;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Properties;

public interface ServiceRequest{

    HttpMethod getHttpMethod();

    URI getUri();

    ByteBuffer getBody();

    Properties getRequestParams();

    /**
     * 返回服务方法的参数值列表；
     *
     * @return
     */
    Object[] getArgs();

}