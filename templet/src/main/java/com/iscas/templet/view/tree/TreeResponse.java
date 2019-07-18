package com.iscas.templet.view.tree;

import com.iscas.templet.common.ResponseEntity;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: zhuquanwen
 * @Description:
 * @Date: 2017/12/25 17:19
 * @Modified:
 **/
@Data
@ToString(callSuper = true)
public class TreeResponse extends ResponseEntity<TreeResponseData> implements Serializable {

}
