package cn.bubi.access.adaptation.blockchain.bc.response.converter;

import cn.bubi.access.adaptation.blockchain.bc.response.Hello;
import cn.bubi.baas.utils.http.HttpServiceContext;
import cn.bubi.baas.utils.http.ResponseConverter;
import cn.bubi.baas.utils.http.agent.ServiceRequest;
import cn.bubi.baas.utils.http.converters.StringResponseConverter;
import cn.bubi.baas.utils.http.util.SerializeUtils;

import java.io.InputStream;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/11/30 上午11:57.
 */
public class HelloResponseConverter implements ResponseConverter{

    @Override
    public Object getResponse(ServiceRequest serviceRequest, InputStream inputStream, HttpServiceContext httpServiceContext) throws Exception{
        String jsonResponse = (String) StringResponseConverter.INSTANCE.getResponse(serviceRequest, inputStream, null);
        if (jsonResponse == null) {
            return null;
        } else {
            jsonResponse = jsonResponse.trim();
            return SerializeUtils.deserializeAs(jsonResponse, Hello.class);
        }
    }

}
