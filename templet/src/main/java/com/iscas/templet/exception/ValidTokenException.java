package com.iscas.templet.exception;

/**
 * 不支持的Token异常
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/7/16 22:21
 * @since jdk1.8
 */
public class ValidTokenException extends BaseException {
    public ValidTokenException() {
        super();
    }

    public ValidTokenException(String message) {
        super(message);
    }

    public ValidTokenException(String message, String msgDetail) {
        super(message, msgDetail);
    }

    public ValidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidTokenException(Throwable cause) {
        super(cause);
    }

    protected ValidTokenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
