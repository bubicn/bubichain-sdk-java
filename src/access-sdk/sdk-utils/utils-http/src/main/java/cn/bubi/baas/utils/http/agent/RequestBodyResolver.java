package cn.bubi.baas.utils.http.agent;

import java.io.InputStream;

interface RequestBodyResolver {
	
	InputStream resolve(Object[] args);
	
}
