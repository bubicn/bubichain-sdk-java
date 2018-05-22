package cn.bubi.baas.utils.http.agent;

import cn.bubi.baas.utils.http.HttpAction;
import cn.bubi.baas.utils.http.ResponseConverter;
import cn.bubi.baas.utils.http.ResponseConverterFactory;
import cn.bubi.baas.utils.http.converters.ByteArrayResponseConverter;
import cn.bubi.baas.utils.http.converters.JsonResponseConverter;
import cn.bubi.baas.utils.http.converters.StringResponseConverter;

import java.lang.reflect.Method;

public class DefaultResponseConverterFactory implements ResponseConverterFactory{

    public static final DefaultResponseConverterFactory INSTANCE = new DefaultResponseConverterFactory();

    private DefaultResponseConverterFactory(){
    }

    @Override
    public ResponseConverter createResponseConverter(HttpAction actionDef, Method mth){
        Class<?> retnClazz = mth.getReturnType();
        // create default response converter;
        if (byte[].class == retnClazz) {
            return ByteArrayResponseConverter.INSTANCE;
        }
        if (String.class == retnClazz) {
            return StringResponseConverter.INSTANCE;
        }

        // TODO:未处理 基本类型、输入输出流；
        return new JsonResponseConverter(retnClazz);
    }

}
