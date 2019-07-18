package com.iscas.templet.view.chart.map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/10 13:59
 * @since jdk1.8
 */

@Setter
@Getter
@AllArgsConstructor
@Deprecated
public class MapSeries implements Serializable {
    protected String name;
    protected List<MapSeriesData> data;
    protected boolean roam = true;
    protected String type = "map"; //类型
    protected String mapType = "china"; //地图类型

    protected Map itemStyle = new HashMap<>(); //写死给前台用的


    public MapSeries() {
        Map label = new HashMap();
        Map show = new HashMap();
        show.put("show", true);
        label.put("label", show);
        itemStyle.put("normal", label);

        Map label2= new HashMap();
        Map show2 = new HashMap();
        show2.put("show", true);
        label2.put("label", show);
        itemStyle.put("emphasis", label2);
    }
}
