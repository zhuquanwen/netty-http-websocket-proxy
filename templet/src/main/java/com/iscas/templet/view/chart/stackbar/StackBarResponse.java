package com.iscas.templet.view.chart.stackbar;

import com.iscas.templet.common.ResponseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/10 13:44
 * @since jdk1.8
 */
@Getter
@Setter
@Deprecated
public class StackBarResponse extends ResponseEntity<StackBarResponseData> implements Serializable {
    protected Object other;

}
