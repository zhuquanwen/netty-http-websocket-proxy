package com.iscas.templet.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义optocon基础异常，继承{@link Exception}<br/>
 * 自定义一些属性,定义这个属性{@link #msgDetail}可以在抛出异常的时候,<br/>
 * 定义一些详细描述,这些信息可以不告诉前台用户，但是可以方便调试<br/>
 * 这些信息可以写入{@link com.iscas.templet.common.ResponseEntity#desc} 中。
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/7/16 21:17
 * @since jdk1.8
 */
@Getter
@Setter
public class BaseException extends Exception {
    /**详细信息描述*/
    private String msgDetail;
    public BaseException() {
        super();
    }

    public BaseException(String message) {
        super(message);
    }
    public BaseException(String message, String msgDetail) {
        super(message);
        this.msgDetail = msgDetail;
    }

    public BaseException(String message, String msgDetail, Throwable e) {
        super(message, e);
        this.msgDetail = msgDetail;
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
        this.msgDetail = cause.getMessage();
    }

    public BaseException(Throwable cause) {
        super(cause);
    }

    protected BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
