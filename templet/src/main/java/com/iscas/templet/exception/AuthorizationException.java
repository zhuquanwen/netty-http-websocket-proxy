package com.iscas.templet.exception;

/**
 * 权限校验失败
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/5 15:39
 * @since jdk1.8
 */
public class AuthorizationException extends BaseException {
    public AuthorizationException() {
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, String msgDetail) {
        super(message, msgDetail);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationException(Throwable cause) {
        super(cause);
    }

    public AuthorizationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
