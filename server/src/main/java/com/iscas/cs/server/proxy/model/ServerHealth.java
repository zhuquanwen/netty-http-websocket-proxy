package com.iscas.cs.server.proxy.model;

import lombok.Data;

/**
 * 服务健康状况实体类
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/4 22:19
 * @since jdk1.8
 */
@Data
public class ServerHealth {
    /**服务前缀 作为key*/
    private String proxyUrl;
    /**版本号*/
    private String version;

    /**版本变更后是否清除缓存*/
    private String cacheRefresh;

    /**健康指标*/
    private String health;
}
