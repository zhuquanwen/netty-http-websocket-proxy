package com.iscas.cs.server.proxy.base;


import com.iscas.cs.server.proxy.model.JettyProps;
import com.iscas.cs.server.proxy.model.ProxyServiceSetting;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.model.ServerHealth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/4 16:33
 * @since jdk1.8
 */
public class Constant {

    /**
     * 服务路由信息
     * */
    public static Map<String, ProxySetting> PROXY_SERVLET_SETTING_MAP = new HashMap<>();
    /**
     * 服务路信息
     * */
    public static ProxyServiceSetting PROXY_SERVICE_SETTING = null;

    /**
     * 服务健康状况
     * */
    public static final Map<String, ServerHealth> SERVER_HEALTH_MAP = new ConcurrentHashMap<>();

    /**
     * 回放线程池Map
     * */
    public static final Map<String, ExecutorService> PLAYBACK_THREADPOOL_MAP = new ConcurrentHashMap<>();

    /**文件下载的KEY*/
    public static final String CS_DOWNFILE = "cs-downfile";

    /**文件下载缓存的key*/
    public static final String DOWNFILE_CACHE_KEY = "download-file";

    /**
     * 缓存服务器直接返回请求HTML格式
     * */
    public static String CS_RES_HTML = null;

    /**
     * 缓存服务器直接返回请求XML格式
     * */
    public static String CS_RES_XML = null;

    /**
     * 缓存服务器直接返回请求JSON格式
     * */
    public static String CS_RES_JSON = null;

    /**
     * jetty配置信息
     * */
    public static JettyProps JETTY_PROPS = null;
}
