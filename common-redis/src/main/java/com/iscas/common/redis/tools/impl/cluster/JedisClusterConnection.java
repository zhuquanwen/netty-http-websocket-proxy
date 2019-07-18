package com.iscas.common.redis.tools.impl.cluster;

import com.iscas.common.redis.tools.ConfigInfo;
import com.iscas.common.redis.tools.JedisConnection;
import com.iscas.common.redis.tools.RedisInfo;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/7 8:37
 * @since jdk1.8
 */
public class JedisClusterConnection implements JedisConnection {
    private ConfigInfo configInfo;
    private JedisCluster jedisCluster = null;
    @Override
    public Object getPool() {
        if(jedisCluster == null){
            synchronized (JedisClusterConnection.class){
                if(jedisCluster == null){
                    List<RedisInfo> redisInfos = configInfo.getRedisInfos();
                    if (redisInfos == null || redisInfos.size() == 0) {
                        throw new RuntimeException("redisInfos不能为空");
                    }
                    Set<HostAndPort> hostAndPortSet = redisInfos.stream().map(redisInfo -> {
                        HostAndPort hostAndPort = new HostAndPort(redisInfo.getHost(), redisInfo.getPort());
                        return hostAndPort;
                    }).collect(Collectors.toSet());

                    jedisCluster = new JedisCluster(hostAndPortSet, configInfo.getClusterTimeout(), configInfo.getClusterSoTimeout(),
                            configInfo.getClusterMaxAttempts(), configInfo.getClusterPassword(), new GenericObjectPoolConfig());

                }
            }
        }
        return jedisCluster;
    }

    @Override
    public void initConfig(ConfigInfo configInfo) {
        this.configInfo = configInfo;
    }

    @Override
    public void close() {

    }
}
