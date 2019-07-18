package com.iscas.templet.view.chart.base;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/5/18 16:29
 * @Modified: 曲线图标题信息
 **/
@Getter
@Setter
@Deprecated
public class ChartTitle implements Serializable {
    protected String text = ""; //标题文本
    protected String link = "";
    protected boolean showtitle = true;
    protected String subtext = "";
    protected String sublink = "";


}
