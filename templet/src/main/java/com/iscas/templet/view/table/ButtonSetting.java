package com.iscas.templet.view.table;

import lombok.Data;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 表格按钮定义
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/4/3 9:19
 * @since jdk1.8
 */
@Data
@ToString(callSuper = true)
public class ButtonSetting {

    /**
     * 对应列名
     * */
    protected String field = "default";

    /**
     * 列对应的一串映射
     * */
    protected Map<Object, List<ButtonMapping>> mapping = new LinkedHashMap<>();
}
