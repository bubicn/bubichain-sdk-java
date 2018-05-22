package cn.bubi.baas.utils.http.converters;

import cn.bubi.baas.utils.http.StringConverter;
import cn.bubi.baas.utils.http.util.SerializeUtils;

/**
 * JSON 格式的参数转换器；
 *
 * @author haiq
 */
public class JsonConverter implements StringConverter{

    private Class<?> dataType;

    public JsonConverter(Class<?> dataType){
        this.dataType = dataType;
    }

    @Override
    public String toString(Object obj){
        // TODO:未定义“日期时间”的输出格式 ；
        return SerializeUtils.serializeToJSON(obj, dataType);
        // return JSON.toJSONString(obj);
    }

}
