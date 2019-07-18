package com.iscas.templet.view.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/5/18 16:33
 * @Modified:
 **/
@Getter
@Setter
@Deprecated
public class ChartAxis implements Serializable {
    protected boolean show = true; //是否显示坐标轴
    protected CurveAxisType type = CurveAxisType.category; //坐标轴类型
    protected String name = ""; //坐标轴名称
    protected Double max; //设置坐标轴的上限和下限，适用于数字坐标轴。两者的缺省值均为[default:null]
    protected Double min;
    protected Integer logBase; //对数轴的底数，只在对数轴中（type: 'log'）有效。[ default: 10 ]
    protected String position = "bottom"; //为X轴的位置
    public enum CurveAxisType {
        /**类目轴，适用于离散的类目数据，为该类型时必须通过 data 设置类目数据。*/
        category,
        /**数值轴，适用于连续数据。*/
        value,
        /**时间轴，适用于连续的时序数据，与数值轴相比时间轴带有时间的格式化，在刻度计算上也有所不同，
         * 例如会根据跨度的范围来决定使用月，星期，日还是小时范围的刻度。*/
        time,
        /**对数轴。适用于对数数据。*/
        log;
    }
}
