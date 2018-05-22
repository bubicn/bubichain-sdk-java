package cn.bubi.access.utils;

import java.util.Properties;

/**
 * PropertiesUtils 定义了从 properties 文件到 pojo 对象的转换方法；
 * <p>
 * 用于对充当配置文件的 properties 文件到定义了配置信息的 POJO 的转换；
 * <p>
 * 支持 properties 的 key 到 POJO 的字段转换，支持层级的 key 的转换，例如： "user.name" 到 user 字段的对象的
 * name 属性；
 *
 * @author haiq
 */
public abstract class PropertiesUtils{
    private PropertiesUtils(){
    }

    /**
     * 合并两个 properties ；
     *
     * @param props 要将其它值合并进来的属性集合；操作将对其产生修改；
     * @param from  属性值将要合并进入其它属性集合；操作不对其产生修改；
     */
    public static void mergeFrom(Properties props, Properties from){
        mergeFrom(props, from, null);
    }

    /**
     * 合并两个 properties ；
     *
     * @param props              要将其它值合并进来的属性集合；操作将对其产生修改；
     * @param from               属性值将要合并进入其它属性集合；操作不对其产生修改；
     * @param propertyNamePrefix 属性名称前缀；
     */
    public static void mergeFrom(Properties props, Properties from, String propertyNamePrefix){
        if (propertyNamePrefix == null || propertyNamePrefix.length() == 0) {
            for (String name : from.stringPropertyNames()) {
                props.setProperty(name, from.getProperty(name));
            }
        } else {
            for (String name : from.stringPropertyNames()) {
                props.setProperty(propertyNamePrefix + name, from.getProperty(name));
            }
        }
    }
}
