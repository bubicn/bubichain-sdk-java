package cn.bubi.blockhcain.adapter;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cn.bubi.blockhcain.adapter.http.Verifier;

public class HttpKit {
    
    private static final String DEFAULT_CHARSET = "UTF-8";
    public static boolean enableSSL = false;

    /**
     * 鍙戦�丟et璇锋眰
     * @param url
     * @return
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     * @throws KeyManagementException 
     */
    public static String get(String url,Boolean https) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, KeyManagementException {
        StringBuffer bufferRes = null;
        TrustManager[] tm = { new MyX509TrustManager() };  
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");  
        sslContext.init(null, tm, new java.security.SecureRandom());  
        // 浠庝笂杩癝SLContext瀵硅薄涓緱鍒癝SLSocketFactory瀵硅薄  
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        
        URL urlGet = new URL(url);
        HttpsURLConnection http = (HttpsURLConnection) urlGet.openConnection();
        // 杩炴帴瓒呮椂
        http.setConnectTimeout(25000);
        // 璇诲彇瓒呮椂 --鏈嶅姟鍣ㄥ搷搴旀瘮杈冩參锛屽澶ф椂闂�
        http.setReadTimeout(25000);
        http.setRequestMethod("GET");
        http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        http.setSSLSocketFactory(ssf);
        http.setHostnameVerifier(new Verifier());
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        
        InputStream in = http.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
        String valueString = null;
        bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null){
            bufferRes.append(valueString);
        }
        in.close();
        if (http != null) {
            // 鍏抽棴杩炴帴
            http.disconnect();
        }
        return bufferRes.toString();
    }
    
    /**
     * 鍙戦�丟et璇锋眰
     * @param url
     * @return
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     * @throws KeyManagementException 
     */
    public static String get(String url) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, KeyManagementException {
    	if(enableSSL){
    		return get(url,true);
    	}else{
    		StringBuffer bufferRes = null;
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
            // 杩炴帴瓒呮椂
            http.setConnectTimeout(25000);
            // 璇诲彇瓒呮椂 --鏈嶅姟鍣ㄥ搷搴旀瘮杈冩參锛屽澶ф椂闂�
            http.setReadTimeout(25000);
            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            
            InputStream in = http.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
            String valueString = null;
            bufferRes = new StringBuffer();
            while ((valueString = read.readLine()) != null){
                bufferRes.append(valueString);
            }
            in.close();
            if (http != null) {
                // 鍏抽棴杩炴帴
                http.disconnect();
            }
            return bufferRes.toString();
    	}
    }

    /**
     *  鍙戦�丟et璇锋眰
     * @param url
     * @param params
     * @return
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String get(String url, Map<String, String> params) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException {
        return get(initParams(url, params));
    }

    /**
     *  鍙戦�丳ost璇锋眰
     * @param url
     * @param params
     * @return
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String post(String url, String params,Boolean https) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
    	StringBuffer bufferRes = null;
        TrustManager[] tm = { new MyX509TrustManager() };
        SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 浠庝笂杩癝SLContext瀵硅薄涓緱鍒癝SLSocketFactory瀵硅薄  
        SSLSocketFactory ssf = sslContext.getSocketFactory();

        URL urlGet = new URL(url);
        HttpsURLConnection http = (HttpsURLConnection) urlGet.openConnection();
        // 杩炴帴瓒呮椂
        http.setConnectTimeout(50000);
        // 璇诲彇瓒呮椂 --鏈嶅姟鍣ㄥ搷搴旀瘮杈冩參锛屽澶ф椂闂�
        http.setReadTimeout(50000);
        http.setRequestMethod("POST");
        http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
        http.setSSLSocketFactory(ssf);
        http.setHostnameVerifier(new Verifier());
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();

        OutputStream out = http.getOutputStream();
        out.write(params.getBytes("UTF-8"));
        out.flush();
        out.close();

        InputStream in = http.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
        String valueString = null;
        bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null){
            bufferRes.append(valueString);
        }
        in.close();
        if (http != null) {
            // 鍏抽棴杩炴帴
            http.disconnect();
        }
        return bufferRes.toString();
    }
    
    /**
     *  鍙戦�丳ost璇锋眰
     * @param url 璇锋眰鍦板潃
     * @param params 璇锋眰鍙傛暟
     * @param https 鏄惁鍚姩https
     * @return
     * @throws IOException 
     * @throws NoSuchProviderException 
     * @throws NoSuchAlgorithmException 
     * @throws KeyManagementException 
     */
    public static String post(String url, String params) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
    	if(enableSSL){
    		return post(url,params,true);
    	}else{
    		StringBuffer bufferRes = null;
	        URL urlGet = new URL(url);
	        HttpURLConnection http = (HttpURLConnection) urlGet.openConnection();
	        // 杩炴帴瓒呮椂
	        http.setConnectTimeout(50000);
	        // 璇诲彇瓒呮椂 --鏈嶅姟鍣ㄥ搷搴旀瘮杈冩參锛屽澶ф椂闂�
	        http.setReadTimeout(50000);
	        http.setRequestMethod("POST");
	        http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
	        http.setDoOutput(true);
	        http.setDoInput(true);
	        http.connect();
	
	        OutputStream out = http.getOutputStream();
	        out.write(params.getBytes("UTF-8"));
	        out.flush();
	        out.close();
	
	        InputStream in = http.getInputStream();
	        BufferedReader read = new BufferedReader(new InputStreamReader(in, DEFAULT_CHARSET));
	        String valueString = null;
	        bufferRes = new StringBuffer();
	        while ((valueString = read.readLine()) != null){
	            bufferRes.append(valueString);
	        }
	        in.close();
	        if (http != null) {
	            // 鍏抽棴杩炴帴
	            http.disconnect();
	        }
	        return bufferRes.toString();
    	}
    }

    /**
     * 鏋勯�爑rl
     * @param url
     * @param params
     * @return
     */
    private static String initParams(String url, Map<String, String> params){
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        } else {
            sb.append("&");
        }
        boolean first = true;
        for (Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=");
            try {
                sb.append(URLEncoder.encode(value, DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
    
    
    
    public static String buildQR(String url,String actionName,Integer sceneId) throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException{
    	String params = "";
    	if("QR_SCENE".equals(actionName)){//涓存椂鏁板瓧鍙傛暟鍊�
    		params = "{\"expire_seconds\": 1800, \"action_name\": \"QR_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": "+sceneId+"}}}";
    	}else if("QR_LIMIT_SCENE".equals(actionName)){//姘镐箙鏁板瓧鍙傛暟鍊�
    		params = "{\"expire_seconds\": 1800, \"action_name\": \"QR_LIMIT_SCENE\", \"action_info\": {\"scene\": {\"scene_id\": "+sceneId+"}}}";
    	}else if("QR_LIMIT_STR_SCENE".equals(actionName)){//姘镐箙鐨勫瓧绗︿覆鍙傛暟鍊�
    		params = "{\"expire_seconds\": 1800, \"action_name\": \"QR_LIMIT_STR_SCENE\", \"action_info\": {\"scene\": {\"scene_str\": \""+sceneId+"\"}}}";
    	}
    	return HttpKit.post(url, params);
    }
    
    public static void main(String[] args) throws Exception {
//    	String fname = "dsasdas.mp4";
//    	String s = fname.substring(0, fname.lastIndexOf("."));
//    	String f = fname.substring(s.length()+1);
//		System.out.println(f);
	}
}

/**
 * 璇佷功绠＄悊
 */
class MyX509TrustManager implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return null;  
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }
}