package cn.bubi.sdk.core.config;

/**
 * @author xiezhengchao@bubi.cn
 * @since 17/12/14 下午6:22.
 * 只是简单的提供基础配置信息
 */
public class SDKProperties{
    private String eventUtis;
    private String ips;

    private boolean accountPoolEnable = false;
    private String address;
    private String publicKey;
    private String privateKey;
    private int size;
    private String poolFilepath;
    private String mark;

    private boolean redisSeqManagerEnable = false;
    private String host;
    private int port;
    private String password;

    public String getEventUtis(){
        return eventUtis;
    }

    public void setEventUtis(String eventUtis){
        this.eventUtis = eventUtis;
    }

    public String getIps(){
        return ips;
    }

    public void setIps(String ips){
        this.ips = ips;
    }

    public boolean isAccountPoolEnable(){
        return accountPoolEnable;
    }

    public void setAccountPoolEnable(boolean accountPoolEnable){
        this.accountPoolEnable = accountPoolEnable;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getPublicKey(){
        return publicKey;
    }

    public void setPublicKey(String publicKey){
        this.publicKey = publicKey;
    }

    public String getPrivateKey(){
        return privateKey;
    }

    public void setPrivateKey(String privateKey){
        this.privateKey = privateKey;
    }

    public int getSize(){
        return size;
    }

    public void setSize(int size){
        this.size = size;
    }

    public String getPoolFilepath(){
        return poolFilepath;
    }

    public void setPoolFilepath(String poolFilepath){
        this.poolFilepath = poolFilepath;
    }

    public String getMark(){
        return mark;
    }

    public void setMark(String mark){
        this.mark = mark;
    }

    public boolean isRedisSeqManagerEnable(){
        return redisSeqManagerEnable;
    }

    public void setRedisSeqManagerEnable(boolean redisSeqManagerEnable){
        this.redisSeqManagerEnable = redisSeqManagerEnable;
    }

    public String getHost(){
        return host;
    }

    public int getPort(){
        return port;
    }

    public void setPort(int port){
        this.port = port;
    }

    public void setHost(String host){
        this.host = host;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }
}
