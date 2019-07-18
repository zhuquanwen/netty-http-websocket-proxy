package com.iscas.templet.exception;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/5/27 22:24
 * @since jdk1.8
 */
public class RepeatSubmitException extends BaseException {
    @Override
    public void setMsgDetail(String msgDetail) {
        super.setMsgDetail(msgDetail);
    }

    @Override
    public String getMsgDetail() {
        return super.getMsgDetail();
    }

    public RepeatSubmitException() {
        super();
    }

    public RepeatSubmitException(String message) {
        super(message);
    }

    public RepeatSubmitException(String message, String msgDetail) {
        super(message, msgDetail);
    }

    public RepeatSubmitException(String message, String msgDetail, Throwable e) {
        super(message, msgDetail, e);
    }

    public RepeatSubmitException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepeatSubmitException(Throwable cause) {
        super(cause);
    }

    protected RepeatSubmitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
