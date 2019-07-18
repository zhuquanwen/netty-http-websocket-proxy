package com.iscas.templet.view.chart.map;

import com.iscas.templet.common.ResponseEntity;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/10 14:08
 * @since jdk1.8
 */
@Getter
@Setter
@Deprecated
public class MapResponse extends ResponseEntity<MapResponseData> implements Serializable {
    protected Object other ;

}
