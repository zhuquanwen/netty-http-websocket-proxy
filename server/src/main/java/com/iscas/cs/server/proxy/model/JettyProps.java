package com.iscas.cs.server.proxy.model;

import lombok.Data;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/15 10:45
 * @since jdk1.8
 */
@Data
public class JettyProps {
    private Integer maxThreads=200;
    private Integer minThreads=8;
    private Integer idleTimeout=60000;
}
