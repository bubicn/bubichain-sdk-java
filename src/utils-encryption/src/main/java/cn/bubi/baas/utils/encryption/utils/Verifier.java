package cn.bubi.baas.utils.encryption.utils;


import javax.net.ssl.SSLSession;


public class Verifier implements javax.net.ssl.HostnameVerifier {

	public boolean verify(String hostname, SSLSession session) {
		return true;
	}

}
