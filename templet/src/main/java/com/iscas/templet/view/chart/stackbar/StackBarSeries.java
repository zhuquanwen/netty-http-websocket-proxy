package com.iscas.templet.view.chart.stackbar;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/10 13:38
 * @since jdk1.8
 */
@Setter
@Getter
@Deprecated
public class StackBarSeries<T> implements Serializable {
    protected String type = "bar"; //暂时使用默认值
    protected String coordinateSystem = "polar"; //暂时使用默认值
    protected String stack = "string"; //暂时使用默认值
    protected List<T> data = new ArrayList<>();
    protected String name; //名称

}
