package com.iscas.templet.view.table;

import lombok.Data;
import lombok.ToString;

/**
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/4/3 9:21
 * @since jdk1.8
 */
@Data
@ToString(callSuper = true)
public class ButtonMapping {
    /**
     * 这个Button对应的URL,如果涉及需要传入其他列数据到路径中，例如/user/@id
     * */
    protected String url;

    protected HttpMethod method = HttpMethod.GET;

    /**
     * Button里显示的值
     * */
    protected String label;


}
