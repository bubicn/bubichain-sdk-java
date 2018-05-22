package cn.bubi.baas.utils.http;

import java.io.InputStream;

/**
 * 参数转换器；
 * 
 * 定义了如何将某个参数从特定类型转换为用于发送 http 请求体的输入流；
 * 
 * @author haiq
 *
 */
public interface RequestBodyConverter {
	
	InputStream toInputStream(Object param);
	
}
