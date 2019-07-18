package com.iscas.templet.view.validator;

import com.iscas.templet.view.table.TableField;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/9/6 18:57
 * @since jdk1.8
 */
@FunctionalInterface
public interface RuleCallback {
    boolean validate(Object obj, TableField tableField);
}
