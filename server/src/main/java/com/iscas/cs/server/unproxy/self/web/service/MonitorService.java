package com.iscas.cs.server.unproxy.self.web.service;

import com.iscas.common.redis.tools.impl.JedisClient;
import com.iscas.cs.server.proxy.util.json.JsonUtils;
import com.iscas.cs.server.redis.RedisConn;
import com.iscas.cs.server.unproxy.self.web.entity.MonitorIndex;
import com.iscas.cs.server.unproxy.self.web.utils.Operate;
import com.iscas.cs.server.unproxy.self.web.utils.ReadLine;
import com.iscas.cs.server.unproxy.self.web.utils.RedisInfoDetail;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.util.Slowlog;

import java.util.*;

public class MonitorService {


    public List<MonitorIndex> getMonitorIndex(){

        List<MonitorIndex> monitorIndices = new ArrayList<>();
        List<String> moniters = ReadLine.readLine();
        for (String moniter:moniters){
            String[] moniterDetail = moniter.split(",");
            MonitorIndex monitorIndex = new MonitorIndex();
            monitorIndex.setId(moniterDetail[0]);
            monitorIndex.setLabel(moniterDetail[1]);
            if(moniterDetail[2].equals("TEXT")){
                monitorIndex.setShowType(MonitorIndex.ShowType.Text);
            }else if(moniterDetail[2].equals("LineChart")){
                monitorIndex.setShowType(MonitorIndex.ShowType.LineChart);
            }else if(moniterDetail[2].equals("PieChart")){
                monitorIndex.setShowType(MonitorIndex.ShowType.PieChart);
            }
            monitorIndex.setTitle(moniterDetail[3]);
            List<String> legends = new ArrayList<>();
            String legend = moniterDetail[4];
            legends = Arrays.asList(legend.split("、"));
            monitorIndex.setLegend(legends);
            monitorIndex.setUnit(moniterDetail[5]);
            monitorIndices.add(monitorIndex);
        }

        return monitorIndices;

    }


    public Map<String,Object> getState(List<String> ids){
        Map<String, Object> map = new HashMap<>();
        List<MonitorIndex> searchMoniter = new ArrayList<>();
        List<MonitorIndex> monitorIndices = getMonitorIndex();
        for (MonitorIndex monitorIndex : monitorIndices) {
            for (String id : ids) {
                if (monitorIndex.getId().equals(id)) {
                    searchMoniter.add(monitorIndex);
                }
            }
        }

        for (MonitorIndex monitorIndex : searchMoniter) {
            if (monitorIndex.getId().equals("basicInfo")) {
                JedisClient jedisClient = RedisConn.getClient();
                JedisCommands commands = jedisClient.getResource();
                Jedis jedis = (Jedis) commands;
                //获取redis服务器信息
                try {
                    String info = jedis.info();
                    List<RedisInfoDetail> ridList = getRedisInfo(info);
                    Map<String,Object> legendMap = new HashMap<>();
                    legendMap.put(monitorIndex.getLegend().get(0),ridList);
                    map.put(monitorIndex.getId(),legendMap);
                }finally {
                    // 返还到连接池
                    jedis.close();
                }
            } else if (monitorIndex.getId().equals("cpuInfo")) {
                JedisClient jedisClient = RedisConn.getClient();
                JedisCommands commands = jedisClient.getResource();
                Jedis jedis = (Jedis) commands;
                try {
                    String info = jedis.info();
                    List<RedisInfoDetail> ridList = getRedisInfo(info);
                    double used_cpu_sys = 0;
                    for (RedisInfoDetail redisInfoDetail:ridList){
                        if(redisInfoDetail.getKey().equals("used_cpu_sys")){
                            used_cpu_sys = Double.parseDouble(redisInfoDetail.getValue().replace("\r",""));
                        }
                    }
                    List<String> legends = monitorIndex.getLegend();
                    Map<String,Object> legendMap = new HashMap<>();
                    for (String legend:legends){
                        if(legend.equals("CPU消耗")){
                            legendMap.put(legend,used_cpu_sys);
                        }
                        map.put(monitorIndex.getId(),legendMap);
                    }
                }finally {
                    jedis.close();
                }
            } else if (monitorIndex.getId().equals("memInfo")) {
                JedisClient jedisClient = RedisConn.getClient();
                JedisCommands commands = jedisClient.getResource();
                Jedis jedis = (Jedis) commands;
                try {
                    List<String> max = jedis.configGet("maxmemory");
                    long maxMem = Long.parseLong(max.get(1));
                    maxMem = maxMem/1024/1024;
                    String info = jedis.info();
                    List<RedisInfoDetail> ridList = getRedisInfo(info);
                    String used_memory_human = "";
                    for (RedisInfoDetail redisInfoDetail:ridList){
                        if(redisInfoDetail.getKey().equals("used_memory_human")){
                            used_memory_human = redisInfoDetail.getValue().replace("\r","");
                        }
                    }
                    List<String> legends = monitorIndex.getLegend();
                    Map<String,Object> legendMap = new HashMap<>();
                    for (String legend:legends){
                        if(legend.equals("内存总量")){
                            legendMap.put(legend,maxMem+"MB");
                        }else {
                            legendMap.put(legend,used_memory_human+"B");
                        }
                        map.put(monitorIndex.getId(),legendMap);
                    }
                }finally {
                    jedis.close();
                }
            } else if (monitorIndex.getId().equals("QPS")) {
                JedisClient jedisClient = RedisConn.getClient();
                JedisCommands commands = jedisClient.getResource();
                Jedis jedis = (Jedis) commands;
                try {
                    String info = jedis.info();
                    List<RedisInfoDetail> ridList = getRedisInfo(info);
                    int QPS = 0;
                    for (RedisInfoDetail redisInfoDetail:ridList){
                        if(redisInfoDetail.getKey().equals("instantaneous_ops_per_sec")){
                            QPS = Integer.parseInt(redisInfoDetail.getValue().replace("\r",""));
                        }
                    }
                    List<String> legends = monitorIndex.getLegend();
                    Map<String,Object> legendMap = new HashMap<>();
                    for (String legend:legends){
                        if(legend.equals("QPS情况")){
                            legendMap.put(legend,QPS);
                        }
                        map.put(monitorIndex.getId(),legendMap);
                    }
                }finally {
                    jedis.close();
                }
            } else if (monitorIndex.getId().equals("client")) {
                JedisClient jedisClient = RedisConn.getClient();
                JedisCommands commands = jedisClient.getResource();
                Jedis jedis = (Jedis) commands;
                try {
                    List<String> max = jedis.configGet("maxclients");
                    String info = jedis.info();
                    List<RedisInfoDetail> ridList = getRedisInfo(info);
                    int connected_clients = 0;
                    for (RedisInfoDetail redisInfoDetail:ridList){
                        if(redisInfoDetail.getKey().equals("connected_clients")){
                            connected_clients = Integer.parseInt(redisInfoDetail.getValue().replace("\r",""));
                        }
                    }
                    List<String> legends = monitorIndex.getLegend();
                    Map<String,Object> legendMap = new HashMap<>();
                    for (String legend:legends){
                        if(legend.equals("总连接数")){
                            legendMap.put(legend,max.get(1));
                        }else {
                            legendMap.put(legend,connected_clients);
                        }
                        map.put(monitorIndex.getId(),legendMap);
                    }
                }finally {
                    jedis.close();
                }

            } else if (monitorIndex.getId().equals("keyNum")) {
                JedisClient jedisClient = RedisConn.getClient();
                JedisCommands commands = jedisClient.getResource();
                Jedis jedis = (Jedis) commands;
                try {
                    long dbSize = jedis.dbSize();
                    List<String> legends = monitorIndex.getLegend();
                    Map<String,Object> legendMap = new HashMap<>();
                    for (String legend:legends){
                        if(legend.equals("keys数量")){
                            legendMap.put(legend,dbSize);
                        }
                        map.put(monitorIndex.getId(),legendMap);
                    }
                }finally {
                    jedis.close();
                }

            } else if (monitorIndex.getId().equals("log")) {
                JedisClient jedisClient = RedisConn.getClient();
                JedisCommands commands = jedisClient.getResource();
                Jedis jedis = (Jedis) commands;
                try {
                    List<Slowlog> list = jedis.slowlogGet(100);
                    List<Operate> opList = null;
                    Operate op  = null;
                    if (list != null && list.size() > 0) {
                        opList = new LinkedList<Operate>();
                        for (Slowlog sl : list) {
                            String args = JsonUtils.toJson(sl.getArgs());
                            if (args.equals("[\"PING\"]") || args.equals("[\"SLOWLOG\",\"get\"]") || args.equals("[\"DBSIZE\"]") || args.equals("[\"INFO\"]")) {
                                continue;
                            }
                            op = new Operate();
                            op.setId(sl.getId());
                            op.setExecuteTime(sl.getExecutionTime()/1000.0 + "ms");
                            op.setUsedTime(sl.getExecutionTime()/1000.0 + "ms");
                            op.setArgs(args);
                            opList.add(op);
                        }
                    }
                    List<String> legends = monitorIndex.getLegend();
                    Map<String,Object> legendMap = new HashMap<>();
                    for (String legend:legends){
                        if(legend.equals("redis的实时日志")){
                            legendMap.put(legend,list);
                        }
                        map.put(monitorIndex.getId(),legendMap);
                    }
                }finally {
                    jedis.close();
                }

            }
        }
        return map;
    }


    public static List<RedisInfoDetail> getRedisInfo(String info){
        List<RedisInfoDetail> ridList = new ArrayList<RedisInfoDetail>();
        String[] strs = info.split("\n");
        RedisInfoDetail rif = null;
        if (strs != null && strs.length > 0) {
            for (int i = 0; i < strs.length; i++) {
                rif = new RedisInfoDetail();
                String s = strs[i];
                String[] str = s.split(":");
                if (str != null && str.length > 1) {
                    String key = str[0];
                    String value = str[1];
                    rif.setKey(key);
                    rif.setValue(value);
                    ridList.add(rif);
                }
            }
        }
        return ridList;
    }

}
