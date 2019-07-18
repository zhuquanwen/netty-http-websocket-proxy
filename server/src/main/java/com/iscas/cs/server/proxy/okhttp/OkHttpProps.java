package com.iscas.cs.server.proxy.okhttp;

import lombok.Data;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/4 21:48
 * @since jdk1.8
 */
@Data
public class OkHttpProps {
    private int readTimeout = 10000; //读取超时时间毫秒
    private int writeTimeout = 10000 ; //写数据超时时间毫秒
    private int connectTimeout = 10000; //连接超时时间毫秒
    private int maxIdleConnection = 15 ; //最大空闲数目
    private long keepAliveDuration = 5 ; //keep alive保持时间 分钟
}
