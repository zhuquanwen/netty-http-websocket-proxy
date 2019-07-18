package com.iscas.cs.server.proxy.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * 回放的对象
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/5 15:03
 * @since jdk1.8
 */
@Data
@Accessors(chain = true)
public class PlaybackRequest implements Serializable {

    /**
     * 对应应用的Key（可以应用之间队列隔离）
     * */
    private String key;

    /**
     * URL
     * */
    private String url;

    /**
     * 请求结构体
     * */
    private byte[] content;

    /**
     * 请求参数
     * */
    private Map<String, String[]> params;

    /**
     * 请求header
     * */
    private Map<String, String> headers;

    /**
     * 请求方式
     * */
    private String method = "GET";

    /**
     * 用户信息
     * */
    //TODO 用户信息暂时没考虑
    private String userInfo;

    /**
     * 数据是否在队列内部
     * */
    private boolean inner = true;
}
