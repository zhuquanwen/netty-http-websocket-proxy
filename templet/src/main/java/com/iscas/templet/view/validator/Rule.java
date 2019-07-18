package com.iscas.templet.view.validator;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/1/23 10:22
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class Rule implements Serializable {
    protected boolean required = false; //是否必须填写值
    protected String reg; //正则表达式
    protected Map<String, Integer> length; //长度  min,max
    protected boolean distinct = false; //是否要校验重复值


}
