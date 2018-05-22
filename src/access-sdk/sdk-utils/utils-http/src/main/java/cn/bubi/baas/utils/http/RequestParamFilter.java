package cn.bubi.baas.utils.http;

import java.util.Properties;

/**
 * 请求参数过滤器；
 * 
 * @author haiq
 *
 */
public interface RequestParamFilter {
	
	void filter(HttpMethod requestMethod, Properties requestParams);
	
}
