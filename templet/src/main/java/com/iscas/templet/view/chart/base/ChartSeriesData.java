package com.iscas.templet.view.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/5/18 16:45
 * @Modified:
 **/
@Getter
@Setter
@Deprecated
public class ChartSeriesData implements Serializable {

    protected String name = ""; //名称适用于pie饼状图，其他图则可为空值, [ default: '' ]

    // 如果是line或者bar图，则value是含两个元素的数组，类型是泛型；第一个是横坐标值，第二个是纵坐标值；
           // 如:[1,0.5]或者["1","0.5"]
            // 如果是bar，则只含有一个元素的数组；
            // 如:[1]或者["1"]
    protected List<Object> value = new ArrayList<>();

    /**扩展字段*/
    protected Object other;


}
