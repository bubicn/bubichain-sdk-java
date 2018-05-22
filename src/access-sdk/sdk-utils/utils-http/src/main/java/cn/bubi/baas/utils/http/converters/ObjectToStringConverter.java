package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.StringConverter;

public class ObjectToStringConverter implements StringConverter{

    @Override
    public String toString(Object param){
        return param == null ? null : param.toString();
    }

}
