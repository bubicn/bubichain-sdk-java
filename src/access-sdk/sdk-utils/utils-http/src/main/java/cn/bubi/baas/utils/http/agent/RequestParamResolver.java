package cn.bubi.baas.utils.http.agent;

import java.util.Properties;

/**
 * 请求参数解析器；
 * 
 * @author haiq
 *
 */
interface RequestParamResolver {
	
	/**
	 * 将方法参数列表解析为请求参数的变量表；
	 * 
	 * @param args 方法参数列表；
	 * @return
	 */
	Properties resolve(Object[] args);
	
}
