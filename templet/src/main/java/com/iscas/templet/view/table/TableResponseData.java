package com.iscas.templet.view.table;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 16:54
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TableResponseData<List> implements Serializable {
    /*返回总条目*/
    protected Long rows;
    /*返回的具体数据，是个集合*/
    private List data;

}
