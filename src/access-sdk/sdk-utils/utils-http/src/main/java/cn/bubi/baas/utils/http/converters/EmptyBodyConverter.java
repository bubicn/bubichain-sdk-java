package cn.bubi.baas.utils.http.converters;

import cn.bubi.access.utils.io.EmptyInputStream;
import cn.bubi.baas.utils.http.RequestBodyConverter;

import java.io.InputStream;

public class EmptyBodyConverter implements RequestBodyConverter{

    @Override
    public InputStream toInputStream(Object param){
        return EmptyInputStream.INSTANCE;
    }

}
