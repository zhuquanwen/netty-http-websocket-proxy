package com.iscas.cs.server.proxy.base;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/9 9:57
 * @since jdk1.8
 */
public enum HttpStatusEnum {
    CACHE_SERVER_ERROR(650, "缓存服务内部错误", "Cache service internal error"),
    REQUEST_CACHED(621, "请求已经被缓存，当服务恢复后会回放此请求", "The request has been cached and will be played back when the service is restored");

    private int status;
    private String msg;
    private String msgEn;

    HttpStatusEnum(int status, String msg, String msgEn) {
        this.status = status;
        this.msg = msg;
        this.msgEn = msgEn;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgEn() {
        return msgEn;
    }

    public void setMsgEn(String msgEn) {
        this.msgEn = msgEn;
    }}
