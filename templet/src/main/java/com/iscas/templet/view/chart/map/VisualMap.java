package com.iscas.templet.view.chart.map;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/10 14:02
 * @since jdk1.8
 */
@Getter
@Setter
@Deprecated
public class VisualMap implements Serializable {
    protected boolean show = true; //暂时使用默认值
    protected String type = "continuous"; //暂时使用默认值
    protected List<String> text = Arrays.asList("low","high"); //暂时使用默认值
    protected String max; //最大值
    protected String min; //最小值
    protected boolean calculate =  false; //是否显示拖拽的手柄

}
