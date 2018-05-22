package cn.bubi.access.utils.net;

/**
 * 网络地址；
 *
 * @author haiq
 */
public class NetworkAddress{

    private String host;

    private int port;

    public NetworkAddress(String host, int port){
        this.host = host;
        this.port = port;
    }

    public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }

    @Override
    public String toString(){
        return host + ":" + port;
    }

}
