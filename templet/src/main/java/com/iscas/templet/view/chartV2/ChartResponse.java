package com.iscas.templet.view.chartV2;

import com.iscas.templet.common.ResponseEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2018/4/11 11:31
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class ChartResponse extends ResponseEntity<ChartResponseData> implements Serializable {
}
