package com.iscas.cs.server.proxy.util;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 8:39
 * @since jdk1.8
 */
public class IdGenerator {
    private IdGenerator() {}

    /**
     *
     * 获取新的队列ID
     */
    public static String getQueueId(String url) {
        StringBuilder sb = new StringBuilder();
        long l = System.currentTimeMillis();
        return sb.append(l).append(url).toString();
    }
}
