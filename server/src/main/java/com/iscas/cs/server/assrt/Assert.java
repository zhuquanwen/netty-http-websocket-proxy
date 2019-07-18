package com.iscas.cs.server.assrt;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/16 21:15
 * @since jdk1.8
 */
public class Assert {
    public void  notNull(Object obj, String msg) {
        if (obj == null) {
            throw new AssertException(msg);
        }
    }
}
