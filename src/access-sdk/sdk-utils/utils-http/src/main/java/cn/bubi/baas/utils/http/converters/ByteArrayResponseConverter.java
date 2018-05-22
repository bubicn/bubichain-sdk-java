package cn.bubi.baas.utils.http.converters;

import cn.bubi.access.utils.spring.StreamUtils;
import cn.bubi.baas.utils.http.HttpServiceContext;
import cn.bubi.baas.utils.http.ResponseConverter;
import cn.bubi.baas.utils.http.agent.ServiceRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ByteArrayResponseConverter implements ResponseConverter{

    public static final ByteArrayResponseConverter INSTANCE = new ByteArrayResponseConverter();

    private ByteArrayResponseConverter(){
    }

    @Override
    public Object getResponse(ServiceRequest request, InputStream responseStream, HttpServiceContext serviceContext){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            StreamUtils.copy(responseStream, out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
