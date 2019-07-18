package com.iscas.templet.view.table;



import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/28 10:19
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class ComboboxData_old<T> implements Serializable {
    /*label*/
    protected String label;
    /*id*/
    protected Object id;
    /*value*/
    protected T value;



}
