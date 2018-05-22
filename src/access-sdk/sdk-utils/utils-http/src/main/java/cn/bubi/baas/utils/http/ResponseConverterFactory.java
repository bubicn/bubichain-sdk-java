package cn.bubi.baas.utils.http;

import java.lang.reflect.Method;

public interface ResponseConverterFactory {

	ResponseConverter createResponseConverter(HttpAction actionDef, Method mth);
	
}
