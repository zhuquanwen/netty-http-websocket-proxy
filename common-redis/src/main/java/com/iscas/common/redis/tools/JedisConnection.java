package com.iscas.common.redis.tools;

/**
 * jedis连接操作接口
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/5 13:58
 * @since jdk1.8
 */
public interface JedisConnection {
    Object getPool();
    void initConfig(ConfigInfo configInfo);
    void close();
}
