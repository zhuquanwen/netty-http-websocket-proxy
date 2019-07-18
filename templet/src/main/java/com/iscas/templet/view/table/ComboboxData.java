package com.iscas.templet.view.table;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/28 10:19
 * @Modified:
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class ComboboxData implements Serializable {
    /*label*/
    protected String label;
    /*value*/
    protected Object value;

}
