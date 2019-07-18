package com.iscas.templet.view.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/5/18 16:50
 * @Modified:
 **/
@Getter
@Setter
@Deprecated
public class ChartSeries implements Serializable {
    protected Integer xAxisIndex = 0; // 如果存在多个x坐标轴，那么每个坐标轴都有一个索引，如果只有一个坐标轴，那么就是0，建议与数组下标想对应

    protected Integer yAxisIndex = 0;
    protected ChartSeriesType type = ChartSeriesType.line; //绘制的图形样式，可以是line(折线)，bar(柱状)，pie（饼状）
    protected List<ChartSeriesData> data = new ArrayList<>(); //详细数据内容

    protected String name; //与legend对应的name

    public enum ChartSeriesType {
        /**折线*/
        line,
        /**柱状*/
        bar,
        /**饼状图*/
        pie;
    }

}
