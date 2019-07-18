package com.iscas.templet.view.table;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 17:08
 * @Modified:
 **/
public enum  TableFieldType {
    /**文本*/
    text,
    /**日期*/
    date,
    /**日期时间*/
    datetime,
    /**时间 HH:mm:ss*/
    time,
    /**没有秒的时间*/
    time_no_s,
    /**颜色*/
    color,
    /**普通下拉列表*/
    select,
    /**图片*/
    image,
    /**数值*/
    number,
    /**链接*/
    link,
    /**嵌套一个数组*/
    array,
    /**嵌套一个对象*/
    object,
    /**嵌套一个tab*/
    tab,
    /**textarea*/
    textarea,
    /**嵌套一颗树*/
    tree,
    /**嵌套一个表格*/
    table,
    /**多选下拉列表*/
    multiSelect,
    /**集合但是以逗号分隔显示*/
    split,
    /**文件选择*/
    file,
    /**按钮 */
    button,
    /**表头下拉*/
    headerSelect;


	public static TableFieldType analyzeFieldType(String type){
		for(TableFieldType fieldType : TableFieldType.values()){
			if(fieldType.name().equalsIgnoreCase(type)){
				return fieldType;
			}
		}
		return null;
	}
}
