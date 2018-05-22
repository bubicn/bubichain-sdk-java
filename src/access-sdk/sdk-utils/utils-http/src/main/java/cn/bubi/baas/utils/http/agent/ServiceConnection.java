package cn.bubi.baas.utils.http.agent;

import java.io.Closeable;

public interface ServiceConnection extends Closeable {
	
	ServiceEndpoint getEndpoint();
	
	@Override
	void close();
	
}
