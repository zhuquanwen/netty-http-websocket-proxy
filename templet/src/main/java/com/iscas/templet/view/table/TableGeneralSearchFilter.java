package com.iscas.templet.view.table;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/27 16:11
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TableGeneralSearchFilter implements Serializable {
    protected Map<String, List> searchFilter;

}
