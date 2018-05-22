package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.HttpServiceConsts;
import cn.bubi.baas.utils.http.RequestBodyConverter;
import cn.bubi.baas.utils.http.util.SerializeUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class JsonBodyConverter implements RequestBodyConverter{

    private Class<?> dataType;

    public JsonBodyConverter(Class<?> dataType){
        this.dataType = dataType;
    }

    @Override
    public InputStream toInputStream(Object param){
        //		String jsonString = JSON.toJSONString(param);
        String jsonString = SerializeUtils.serializeToJSON(param, dataType);
        try {
            return new ByteArrayInputStream(jsonString.getBytes(HttpServiceConsts.CHARSET));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
