package com.iscas.templet.view.table;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 17:10
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TableSetting implements Serializable {
    /*是否显示复选框*/
    protected Boolean checkbox = false;
    /*表前说明*/
    protected String frontInfo;
    /*表后说明*/
    protected String backInfo;
    /*表的标题*/
    protected String title;
    /*表的显示形式*/
    protected TableViewType viewType = TableViewType.multi;

    /**单元格内可不可编辑*/
    protected boolean cellEditable = false;

    /**
     * 数据列自生成列说明
     * */
    protected List<ButtonSetting> buttonSetting;
}
