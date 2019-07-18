package com.iscas.templet.view.chart.map;

import com.iscas.templet.view.chart.base.ChartTitle;
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
 * @date 2018/9/10 13:48
 * @since jdk1.8
 */
@Getter
@Setter
@Deprecated
public class MapResponseData implements Serializable {
    protected ChartTitle title = new ChartTitle();
    protected List<MapSeries> series = new ArrayList<>();
    protected VisualMap visualMap = new VisualMap();

}
