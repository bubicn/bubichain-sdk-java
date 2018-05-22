package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.RequestBodyConverter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ByteArrayBodyConverter implements RequestBodyConverter{

    @Override
    public InputStream toInputStream(Object param){
        byte[] bytes = (byte[]) param;
        return new ByteArrayInputStream(bytes);
    }

}
