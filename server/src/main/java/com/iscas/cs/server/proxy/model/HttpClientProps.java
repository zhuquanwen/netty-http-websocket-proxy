package com.iscas.cs.server.proxy.model;

import com.iscas.cs.server.proxy.util.ConfigUtils;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/10 15:09
 * @since jdk1.8
 */
@Data
@Accessors(chain = true)
public class HttpClientProps {
    /**
     * 是否允许自动重定向
     * */
    protected boolean doHandleRedirects = false;
    /**HTTPCLIENT cookie策略*/
    protected String cookieSpecs = "ignoreCookies";
    /**
     * 服务端响应超时时间设置(单位:毫秒)
     */
    protected int connectTimeout = 20000;
    /**
     * 向服务端请求超时时间设置(单位:毫秒)
     */
    protected int readTimeout = 20000;
    /*从连接池获取连接的超时时间*/
    protected int connectionRequestTimeout = 20000;
    /**httpclient最大连接数*/
    protected int maxTotal = 200;
    /**httpclient每个路由的最大连接数*/
    protected int maxPerRoute = 200;
    /**是否关闭socket缓冲*/
    protected boolean tcpNoDelay = false;
    /**关闭socket后，是否可立即重用端口*/
    protected boolean soReuseAddress = true;
    /**接收数据等待超时时间毫秒*/
    protected int soTimeout = 20000;
    /**socket最大等待关闭连接时间秒*/
    protected int soLinger = 60;
    /**重试次数*/
    protected int retry = 1;


    public static HttpClientProps getInstance() {
        return HttpClientPropsHolder.instance;
    }

    private static class HttpClientPropsHolder {
        public static final HttpClientProps instance = ConfigUtils.readHttpClientProps();
    }
}
