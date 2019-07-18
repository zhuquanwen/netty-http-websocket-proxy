package com.iscas.templet.view.chart.base;

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
 * @date 2018/9/11 20:00
 * @since jdk1.8
 */
@Getter
@Setter
@Deprecated
public class MyChartResponseData implements Serializable, Cloneable {
    protected ChartTitle title = new ChartTitle();
    protected List<ChartAxis> xAxis = new ArrayList<>();
    protected List<ChartAxis> yAxis = new ArrayList<>();
    protected List<ChartSeries> series = new ArrayList<>();

    protected Legend legend = new Legend();
    protected Object others;


}
