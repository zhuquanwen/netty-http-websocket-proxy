package com.iscas.cs.server.redis;

import com.iscas.common.redis.tools.ConfigInfo;
import com.iscas.common.redis.tools.JedisConnection;
import com.iscas.common.redis.tools.RedisInfo;
import com.iscas.common.redis.tools.impl.JedisClient;
import com.iscas.common.redis.tools.impl.standalone.JedisStandAloneConnection;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

/**
 * Redis连接获取-单例模式
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/2 18:45
 * @since jdk1.8
 */
@Slf4j
public class RedisConn {
    private RedisConn() {}
    public volatile static JedisClient jedisClient;

    public static JedisClient getClient() {
        if (jedisClient == null) {
            synchronized (RedisConn.class) {
                if (jedisClient == null) {
                    log.debug("读取redis配置文件-redis.properties");
                    Properties prop = new Properties();
                    try {
                        @Cleanup InputStream is = RedisConn.class.getResourceAsStream("/redis.properties");
                        prop.load(is);
                    } catch (IOException e) {
                        log.error("获取redis配置出错", e);
                        throw new RuntimeException("获取redis配置文件出错", e);
                    }
                    String host = prop.getProperty("host");
                    String port = prop.getProperty("port");
                    String pwd = prop.getProperty("pwd");
                    String timeout = prop.getProperty("timeout");
                    String maxWait = prop.getProperty("maxWait");
                    String maxTotal = prop.getProperty("maxTotal");
                    String maxIdle = prop.getProperty("maxIdle");
                    JedisConnection jedisConnection = new JedisStandAloneConnection();
                    ConfigInfo configInfo = new ConfigInfo();
                    configInfo.setMaxIdle(Integer.valueOf(maxIdle));
                    configInfo.setMaxTotal(Integer.valueOf(maxTotal));
                    configInfo.setMaxWait(Long.valueOf(maxWait));
                    RedisInfo redisInfo = new RedisInfo();
                    redisInfo.setHost(host);
                    redisInfo.setPort(Integer.valueOf(port));
                    if (StringUtils.isNotEmpty(pwd)) {
                        redisInfo.setPwd(pwd);
                    }
                    redisInfo.setTimeout(Integer.valueOf(timeout));
                    configInfo.setRedisInfos(Arrays.asList(redisInfo));
                    jedisClient = new JedisClient(jedisConnection, configInfo);
                    log.info("获取redis新连接成功");
                }
            }
        }
        return jedisClient;
    }

}
