package com.iscas.templet.view.table;

import com.iscas.templet.view.validator.Rule;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 17:07
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TableField implements Serializable {
    /*表字段名称*/
    protected String field;
    /*表字段显示名称*/
    protected String header;
    /*是否可编辑，默认不可编辑*/
    protected boolean editable = true;
    /*此列是否支持排序，默认不支持*/
    protected boolean sortable = false;
    /*此列的类型*/
    protected TableFieldType type = TableFieldType.text;
    /*如果是下拉列表,返回的下拉列表信息*/
    protected List<ComboboxData> option ;
    /**是否查询*/
    protected boolean search = false;
    /*查询方式*/
    protected TableSearchType searchType = TableSearchType.exact;
    /*是不是link*/
    protected boolean link = false;

    /**是不是可以新增*/
    protected boolean addable = true;

    /**是不是隐藏*/
    protected boolean hidden = false;

    /**嵌套类型，super 名*/
    protected String parent;

    /*校验规则*/
    protected Rule rule;

    /**搜索方式*/
    protected String searchWay = null;

    /**
     * 如果是下拉列表,下拉列表的URL
     * */
    protected String selectUrl = null;
}
