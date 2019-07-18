package com.iscas.common.redis.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/5 16:15
 * @since jdk1.8
 */
public class ConfigInfo {
    private int maxTotal = 50;
    private int maxIdle = 5;
    private long maxWait = 20000;
    private List<RedisInfo> redisInfos = new ArrayList<>();

    //集群用的
    private int clusterTimeout = 5000;
    private int clusterSoTimeout = 5000;
    private int clusterMaxAttempts = 50;
    private String clusterPassword = null;

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        this.maxWait = maxWait;
    }

    public List<RedisInfo> getRedisInfos() {
        return redisInfos;
    }

    public void setRedisInfos(List<RedisInfo> redisInfos) {
        this.redisInfos = redisInfos;
    }

    public int getClusterTimeout() {
        return clusterTimeout;
    }

    public void setClusterTimeout(int clusterTimeout) {
        this.clusterTimeout = clusterTimeout;
    }

    public int getClusterSoTimeout() {
        return clusterSoTimeout;
    }

    public void setClusterSoTimeout(int clusterSoTimeout) {
        this.clusterSoTimeout = clusterSoTimeout;
    }

    public int getClusterMaxAttempts() {
        return clusterMaxAttempts;
    }

    public void setClusterMaxAttempts(int clusterMaxAttempts) {
        this.clusterMaxAttempts = clusterMaxAttempts;
    }

    public String getClusterPassword() {
        return clusterPassword;
    }

    public void setClusterPassword(String clusterPassword) {
        this.clusterPassword = clusterPassword;
    }
}
