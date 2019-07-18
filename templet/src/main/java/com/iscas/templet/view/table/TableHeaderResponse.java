package com.iscas.templet.view.table;

import com.iscas.templet.common.ResponseEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 17:03
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TableHeaderResponse extends ResponseEntity<TableHeaderResponseData> implements Serializable {

}
