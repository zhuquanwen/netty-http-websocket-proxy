package com.iscas.templet.view.chart.base;

import com.iscas.templet.common.ResponseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/4/11 11:31
 * @Modified:
 **/
@Getter
@Setter
@Deprecated
public class ChartResponse extends ResponseEntity<ChartResponseData> implements Serializable {
    protected Object other;

}
