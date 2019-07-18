package com.iscas.common.redis.tools;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/6 13:59
 * @since jdk1.8
 */
public class RedisInfo {
    private String host;
    private int port = 6379;
    private int timeout = 2000;
    private String pwd;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public RedisInfo(String host, int port, int timeout, String pwd) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.pwd = pwd;
    }

    public RedisInfo() {
    }
}
