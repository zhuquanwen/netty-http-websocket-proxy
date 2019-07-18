package com.iscas.templet.exception;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/10/26 17:54
 * @since jdk1.8
 */
public class AuthorizationRuntimeException extends BaseRuntimeException {
    public AuthorizationRuntimeException() {
    }

    public AuthorizationRuntimeException(String message) {
        super(message);
    }

    public AuthorizationRuntimeException(String message, String msgDetail) {
        super(message, msgDetail);
    }

    public AuthorizationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthorizationRuntimeException(Throwable cause) {
        super(cause);
    }

    public AuthorizationRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
