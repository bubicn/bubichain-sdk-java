package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.HttpServiceContext;
import cn.bubi.baas.utils.http.ResponseConverter;
import cn.bubi.baas.utils.http.agent.ServiceRequest;

import java.io.InputStream;

public class NullResponseConverter implements ResponseConverter{

    public static final ResponseConverter INSTANCE = new NullResponseConverter();

    private NullResponseConverter(){
    }

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext){
        return null;
    }

}
