package com.iscas.templet.common;

/**
 * Controller基础控制类
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/7/16
 * @since jdk1.8
 */
public class BaseController {
    /**
     * 获取返回模板
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @return com.iscas.templet.common.ResponseEntity
     */
    public ResponseEntity getResponse() {
        return new ResponseEntity();
    }

    /**
     * 获取返回模板
     * @version 1.0
     * @since jdk1.8
     * @date 2018/7/16
     * @param tClass 返回的泛型Class
     * @return com.iscas.templet.common.ResponseEntity
     */
    public <T> ResponseEntity<T> getResponse(Class<T> tClass) {
        return new ResponseEntity<T>();
    }

}
