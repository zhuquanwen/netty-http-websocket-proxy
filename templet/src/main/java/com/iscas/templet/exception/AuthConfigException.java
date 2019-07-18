package com.iscas.templet.exception;


/**
 * 权限校验配置读取异常
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/7/16 21:15
 * @since jdk1.8
 */
public class AuthConfigException extends BaseException {
    public AuthConfigException() {
        super();
    }

    public AuthConfigException(String message) {
        super(message);
    }

    public AuthConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthConfigException(Throwable cause) {
        super(cause);
    }

    protected AuthConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public AuthConfigException(String message, String msgDetail) {
        super(message, msgDetail);
    }
}
