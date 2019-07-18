package com.iscas.common.redis.tools.impl.standalone;

import com.iscas.common.redis.tools.ConfigInfo;
import com.iscas.common.redis.tools.JedisConnection;
import com.iscas.common.redis.tools.RedisInfo;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

/**
 * 单机模式获取Jedis连接
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/5 13:52
 * @since jdk1.8
 */
public class JedisStandAloneConnection implements JedisConnection {
    private JedisPool jedisPool = null;
    private ConfigInfo configInfo;
    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();


    @Override
    public void initConfig(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public Object getPool() {
        if(jedisPool == null){
            synchronized (JedisStandAloneConnection.class){
                if(jedisPool == null){
                    jedisPoolConfig.setMaxTotal(configInfo.getMaxTotal());
                    jedisPoolConfig.setMaxIdle(configInfo.getMaxIdle());
                    jedisPoolConfig.setMaxWaitMillis(configInfo.getMaxWait());
                    List<RedisInfo> redisInfos = configInfo.getRedisInfos();
                    if (redisInfos == null || redisInfos.size() == 0) {
                        throw new RuntimeException("redisInfos不能为空");
                    }
                    RedisInfo redisInfo = redisInfos.get(0);
                    if (redisInfo.getPwd() != null) {
                        jedisPool = new JedisPool(jedisPoolConfig, redisInfo.getHost(),
                                redisInfo.getPort(), redisInfo.getTimeout(), redisInfo.getPwd());
                    } else {
                        jedisPool = new JedisPool(jedisPoolConfig, redisInfo.getHost(),
                                redisInfo.getPort(), redisInfo.getTimeout());
                    }

                }
            }
        }
        return jedisPool;
    }

    @Override
    public synchronized void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }



}
