package com.iscas.common.redis.tools.impl.shard;

import com.iscas.common.redis.tools.ConfigInfo;
import com.iscas.common.redis.tools.JedisConnection;
import com.iscas.common.redis.tools.RedisInfo;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Share模式获取Jedis连接
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/5 14:21
 * @since jdk1.8
 */
public class JedisShardConnection implements JedisConnection {
    private ShardedJedisPool jedisPool = null;
    private ConfigInfo configInfo;
    private JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();

    @Override
    public Object getPool() {
        if(jedisPool == null){
            synchronized (JedisShardConnection.class){
                if(jedisPool == null){
                    jedisPoolConfig.setMaxTotal(configInfo.getMaxTotal());
                    jedisPoolConfig.setMaxIdle(configInfo.getMaxIdle());
                    jedisPoolConfig.setMaxWaitMillis(configInfo.getMaxWait());
                    List<RedisInfo> redisInfos = configInfo.getRedisInfos();
                    if (redisInfos == null || redisInfos.size() == 0) {
                        throw new RuntimeException("redisInfos不能为空");
                    }
                    List<JedisShardInfo> shardInfos = redisInfos.stream().map(redisInfo -> {
                        JedisShardInfo jedisShardInfo = new JedisShardInfo(redisInfo.getHost(), redisInfo.getPort(),
                                redisInfo.getTimeout());
                        jedisShardInfo.setPassword(redisInfo.getPwd());
                        return jedisShardInfo;
                    }).collect(Collectors.toList());

                    jedisPool = new ShardedJedisPool(jedisPoolConfig, shardInfos);
                }
            }
        }
        return jedisPool;
    }

    @Override
    public void initConfig(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public synchronized void close() {
        if (jedisPool != null && !jedisPool.isClosed()) {
            jedisPool.close();
        }
    }

}
