package com.iscas.templet.view.table;

import com.iscas.templet.common.ResponseEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 16:40
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TableResponse extends ResponseEntity<TableResponseData> implements Serializable {
    public TableResponse(){}
    public TableResponse(Integer status, String message) {
        super(status, message);
    }
}
