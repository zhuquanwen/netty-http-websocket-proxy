package com.iscas.cs.server.unproxy.self.web.entity;

import java.util.List;

/**
 * @Author: DataDong
 * @Descrition: 监控指标项描述
 * @Date: Create in 2018/12/27 9:54
 * @Modified By:
 */
public class MonitorIndex {
	public enum ShowType{
		/*纯文本展示，数据格式为单层map图的id-》当前值*/
		Text,

		/*曲线图展示，图的id-》图例-》当前值*/
		LineChart,

		/*饼状图展示，图的id-》图例-》当前值*/
		PieChart
	}

	/*一个监控项的唯一id*/
	private String id;
	/*别名*/
	private String label;
	/*展示类型*/
	private ShowType showType;
	/*标题*/
	private String title;
	/*图例*/
	private List<String> legend;

	/*纵轴单位，可以为空，如GB*/
	private String unit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ShowType getShowType() {
		return showType;
	}

	public void setShowType(ShowType showType) {
		this.showType = showType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<String> getLegend() {
		return legend;
	}

	public void setLegend(List<String> legend) {
		this.legend = legend;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
