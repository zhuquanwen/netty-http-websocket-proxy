package com.iscas.templet.common;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 16:41
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class ResponseEntity<T> implements Serializable {

    /**
     * http状态码
     */
//    protected Integer status;
    /**
     * 状态信息
     */
    protected String message;

    /**
     * 服务器内部错误描述
     */
    protected String desc;

    /**
     * 返回值
     */
    protected T value;

    /**
     * 访问URL
     */
    protected String requestURL;

    protected long tookInMillis;

    protected int total;

    public ResponseEntity(Integer status, String message) {
        super();
//        this.status = status;
        this.message = message;
    }

    public ResponseEntity() {
        super();
//        this.status = 200;
        this.message = "操作成功";
    }
    public ResponseEntity(String message){
        super();
        this.message = message;
    }


}
