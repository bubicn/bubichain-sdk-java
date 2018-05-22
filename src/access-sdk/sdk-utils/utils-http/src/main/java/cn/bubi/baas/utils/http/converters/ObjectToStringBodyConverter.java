package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.HttpServiceConsts;
import cn.bubi.baas.utils.http.RequestBodyConverter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class ObjectToStringBodyConverter implements RequestBodyConverter{

    @Override
    public InputStream toInputStream(Object param){
        try {
            String text = param.toString();
            byte[] bytes = text.getBytes(HttpServiceConsts.CHARSET);
            return new ByteArrayInputStream(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
