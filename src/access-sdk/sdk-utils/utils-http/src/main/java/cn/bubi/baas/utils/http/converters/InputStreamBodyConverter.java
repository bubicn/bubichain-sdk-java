package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.RequestBodyConverter;

import java.io.InputStream;

public class InputStreamBodyConverter implements RequestBodyConverter{

    @Override
    public InputStream toInputStream(Object param){
        return (InputStream) param;
    }

}
