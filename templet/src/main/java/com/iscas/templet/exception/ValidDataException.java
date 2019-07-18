package com.iscas.templet.exception;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/6 19:13
 * @since jdk1.8
 */
public class ValidDataException extends BaseException {
    public ValidDataException() {
    }

    public ValidDataException(String message) {
        super(message);
    }

    public ValidDataException(String message, String msgDetail) {
        super(message, msgDetail);
    }

    public ValidDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidDataException(Throwable cause) {
        super(cause);
    }

    public ValidDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
