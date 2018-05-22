package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.HttpServiceContext;
import cn.bubi.baas.utils.http.ResponseConverter;
import cn.bubi.baas.utils.http.agent.ServiceRequest;
import cn.bubi.baas.utils.http.util.SerializeUtils;

import java.io.InputStream;

public class JsonResponseConverter implements ResponseConverter{

    private Class<?> clazz;

    public JsonResponseConverter(Class<?> clazz){
        this.clazz = clazz;
    }

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext) throws Exception{
        String jsonResponse = (String) StringResponseConverter.INSTANCE.getResponse(request, responseStream, null);
        if (jsonResponse == null) {
            return null;
        }
        jsonResponse = jsonResponse.trim();
        // TODO: 未指定“日期时间”格式的策略；
        return SerializeUtils.deserializeAs(jsonResponse, clazz);
        //		return JSON.toJavaObject(JSONObject.parseObject(jsonResponse), clazz);
    }

}
