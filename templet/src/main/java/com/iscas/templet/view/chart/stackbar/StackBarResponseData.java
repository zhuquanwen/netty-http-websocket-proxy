package com.iscas.templet.view.chart.stackbar;

import com.iscas.templet.view.chart.base.ChartTitle;
import com.iscas.templet.view.chart.base.Legend;
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
 * @date 2018/9/10 11:32
 * @since jdk1.8
 */
@Getter
@Setter
@Deprecated
public class StackBarResponseData  implements Serializable {
    protected StackBarAngleAxis angleAxis;
    protected List<StackBarSeries> series = new ArrayList<>();
    protected ChartTitle title = new ChartTitle();
    protected Legend legend = new Legend();
    protected Object others;

}
