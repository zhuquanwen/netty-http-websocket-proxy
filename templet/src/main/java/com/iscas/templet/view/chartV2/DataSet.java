package com.iscas.templet.view.chartV2;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/3/12 8:54
 * @since jdk1.8
 */
@Data
@ToString(callSuper = true)
public class DataSet {
    /**
     * 数据
     * */
    protected List<List<? extends Object>> source = new ArrayList<>();
}
