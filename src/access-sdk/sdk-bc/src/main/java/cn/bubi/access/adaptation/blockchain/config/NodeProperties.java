package cn.bubi.access.adaptation.blockchain.config;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/10/24 上午11:23.
 */
public class NodeProperties{

    private String host;

    private int port;

    private boolean https = false;


    public NodeProperties(String host, int port, boolean https){
        this.host = host;
        this.port = port;
        this.https = https;
    }

    public String getHost(){
        return host;
    }

    public void setHost(String host){
        this.host = host;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public boolean isHttps(){
        return https;
    }

    public void setHttps(boolean https){
        this.https = https;
    }

}