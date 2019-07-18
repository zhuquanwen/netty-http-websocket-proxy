package com.iscas.common.redis.tools.impl;


import com.iscas.common.redis.tools.ConfigInfo;
import com.iscas.common.redis.tools.IJedisClient;
import com.iscas.common.redis.tools.JedisConnection;
import com.iscas.common.redis.tools.helper.MyObjectHelper;
import com.iscas.common.redis.tools.helper.MyStringHelper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * JedisClient
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/5 14:46
 * @since jdk1.8
 */
public class JedisClient implements IJedisClient {
    private Object jedisPool;
    public JedisClient(JedisConnection jedisConnection, ConfigInfo configInfo) {
        jedisConnection.initConfig(configInfo);
        jedisPool = jedisConnection.getPool();
    }

    /**
     * 获取数据，获取字符串数据
     * @param key 键
     * @return 值
     */
    @Override
    public String get(String key) {
        String value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.get(key);
                value = MyStringHelper.isNotBlank(value) && !"nil".equalsIgnoreCase(value) ? value : null;
            }
        }finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取数据，获取对象数据，需经过反序列化
     * @param key 键
     * @return 值
     */
    @Override
    public Object getObject(String key) throws IOException, ClassNotFoundException {
        Object value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                value = toObject(jedisCommandsBytesGet(jedis, getBytesKey(key)));
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置数据，字符串数据
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  boolean set(String key, String value, int cacheSeconds) {
        String result = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } finally {
            returnResource(jedis);
        }
        return "OK".equals(result);
    }

    /**
     * 设置数据，对象数据，序列化后存入redis
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  boolean setObject(String key, Object value, int cacheSeconds) throws IOException {
        String result = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedisCommandsBytesSet(jedis, getBytesKey(key), toBytes(value));
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } finally {
            returnResource(jedis);
        }
        return "OK".equals(result);
    }

    /**
     * 获取List数据，List中数据为字符串
     * @param key 键
     * @return 值
     */
    @Override
    public List<String> getList(String key) {
        List<String> value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lrange(key, 0, -1);
            }
        }  finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取List数据，List中为对象，经过反序列化
     * @param key 键
     * @return 值
     */
    @Override
    public List<Object> getObjectList(String key) throws IOException, ClassNotFoundException {
        List<Object> value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                List<byte[]> list = jedisCommandsBytesLrange(jedis, getBytesKey(key));
                value = new ArrayList<>();
                for (byte[] bs : list){
                    value.add(toObject(bs));
                }
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置List数据，List中值为字符串
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  long setList(String key, List<String> value, int cacheSeconds) {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.rpush(key, (String[])value.toArray());
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置List数据，List中数据为对象，经过序列化后存储
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  long setObjectList(String key, List<Object> value, int cacheSeconds) throws IOException {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                jedis.del(key);
            }
            if (value == null || value.size() == 0) {
                throw new RuntimeException("不能传入空集合");
            }
            byte[][] list = new byte[value.size()][];
            for (int i = 0; i< value.size(); i++){
                list[i] = toBytes(value.get(i));
            }
            result = jedisCommandsBytesRpush(jedis, getBytesKey(key), list);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }

        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List中添加值，添加的值为字符串
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public  long listAdd(String key, String... value) {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedis.rpush(key, value);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向List中添加值，添加的值为对象
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public long listObjectAdd(String key, Object... value) throws IOException {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (value == null || value.length == 0) {
                throw new RuntimeException("不能传入空集合");
            }
            byte[][] list = new byte[value.length][];
            for (int i = 0; i< value.length; i++){
                list[i] = toBytes(value[i]);
            }
            result = jedisCommandsBytesRpush(jedis, getBytesKey(key), list);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     *  从左边pop数据，适用于队列
     * @version 1.0
     * @since jdk1.8
     * @date 2018/11/6
     * @param key
     * @throws
     * @return java.lang.String
     */
    @Override
    public String lpopList(String key) {
        String value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.lpop(key);
            }
        }  finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 从左边pop数据，适用于队列，pop出对象
     * @version 1.0
     * @since jdk1.8
     * @date 2018/11/6
     * @param key
     * @throws
     * @return java.lang.Object
     */
    @Override
    public Object lpopObjectList(String key) throws IOException, ClassNotFoundException {
        Object value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                byte[] data = jedisCommandsBytesLpop(jedis, getBytesKey(key));
                value = toObject(data);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     *  从右边pop数据，适用于栈
     * @version 1.0
     * @since jdk1.8
     * @date 2018/11/6
     * @param key
     * @throws
     * @return java.lang.String
     */
    @Override
    public String rpopList(String key) {
        String value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.rpop(key);
            }
        }  finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 从右边pop数据，适用于栈，pop出对象
     * @version 1.0
     * @since jdk1.8
     * @date 2018/11/6
     * @param key
     * @throws
     * @return java.lang.Object
     */
    @Override
    public Object rpopObjectList(String key) throws IOException, ClassNotFoundException {
        Object value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                byte[] data = jedisCommandsBytesRpop(jedis, getBytesKey(key));
                value = toObject(data);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }


    /**
     * 获取集合，类型为字符串
     * @param key 键
     * @return 值
     */
    @Override
    public Set<String> getSet(String key) {
        Set<String> value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.smembers(key);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 获取集合数据，类型为对象
     * @param key 键
     * @return 值
     */
    @Override
    public Set<Object> getObjectSet(String key) throws IOException, ClassNotFoundException {
        Set<Object> value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                value = new HashSet<>();
                Set<byte[]> set = jedisCommandsBytesSmembers(jedis, getBytesKey(key));
                for (byte[] bs : set){
                    value.add(toObject(bs));
                }
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Set, 值为字符串类型
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  long setSet(String key, Set<String> value, int cacheSeconds) {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            if (value == null || value.size() == 0 ) {
                throw new RuntimeException("集合不能为空");
            }
            String[] strs = new String[value.size()];
            int i = 0;
            for (String str: value) {
                strs[i++] = str;
            }
            result = jedis.sadd(key, strs);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 设置Set，值为任意对象类型
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  long setObjectSet(String key, Set<Object> value, int cacheSeconds) throws IOException {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                jedis.del(key);
            }
            byte[][] bytes = new byte[value.size()][];
            int i = 0;
            for (Object obj: value) {
                bytes[i++] = toBytes(obj);
            }
            result = jedisCommandsBytesSadd(jedis, getBytesKey(key), bytes);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set中追加值，值为字符串
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public  long setSetAdd(String key, String... value) {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedis.sadd(key, value);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 向Set中追加值，类型为对象
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public  long setSetObjectAdd(String key, Object... value) throws IOException {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            byte[][] bytes = new byte[value.length][];
            int i = 0;
            for (Object obj: value) {
                bytes[i++] = toBytes(obj);
            }
            result = jedisCommandsBytesSadd(jedis, getBytesKey(key), bytes);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 获取Map
     * @param key 键
     * @return 值
     */
    @Override
    public Map<String, String> getMap(String key) {
        Map<String, String> value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                value = jedis.hgetAll(key);
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    @Override
    public Map<byte[], byte[]> getBytesMap(byte[] key) {
        Map<byte[], byte[]> value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                Jedis jd = (Jedis) jedis;
                if (jd.exists(key)) {
                    value = jd.hgetAll(key);
                }
            }

        } finally {
            returnResource(jedis);
        }
        return value;

    }

    /**
     * 获取Map 类型为对象
     * @param key 键
     * @return 值
     */
    @Override
    public Map<String, Object> getObjectMap(String key) throws IOException, ClassNotFoundException {
        Map<String, Object> value = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                value = new HashMap<>();
                Map<byte[], byte[]> map = jedisCommandsBytesHgetall(jedis, getBytesKey(key));
                for (Map.Entry<byte[], byte[]> e : map.entrySet()){
                    value.put(MyStringHelper.toString(e.getKey()), toObject(e.getValue()));
                }
            }
        } finally {
            returnResource(jedis);
        }
        return value;
    }

    /**
     * 设置Map, 类型为字符串
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  boolean setMap(String key, Map<String, String> value, int cacheSeconds) {
        String result = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)) {
                jedis.del(key);
            }
            result = jedis.hmset(key, value);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } finally {
            returnResource(jedis);
        }
        return "OK".equals(result);
    }

    @Override
    public boolean setBytesMap(byte[] key, Map<byte[], byte[]> value, int cacheSeconds) {
        String result = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                Jedis jd = (Jedis) jedis;
                if (jd.exists(key)){
                    jd.del(key);
                }
                result = jd.hmset(key, value);
                if (cacheSeconds != 0) {
                    jd.expire(key, cacheSeconds);
                }
            }
//            if (jedis.exists(key)) {
//                jedis.del(key);
//            }
//            result = jedis.hmset(key, value);
//            if (cacheSeconds != 0) {
//                jedis.expire(key, cacheSeconds);
//            }
        } finally {
            returnResource(jedis);
        }
        return "OK".equals(result);
    }

    /**
     * 设置Map 类型为对象
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    @Override
    public  boolean setObjectMap(String key, Map<String, Object> value, int cacheSeconds) throws IOException {
        String result = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))) {
                jedis.del(key);
            }
            Map<byte[], byte[]> map = new HashMap<>();
            for (Map.Entry<String, Object> e : value.entrySet()){
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedisCommandsBytesHmset(jedis, getBytesKey(key), (Map<byte[], byte[]>)map);
            if (cacheSeconds != 0) {
                jedis.expire(key, cacheSeconds);
            }
        } finally {
            returnResource(jedis);
        }
        return "OK".equals(result);
    }

    /**
     * 向Map中添加值 类型为字符串
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public  boolean mapPut(String key, Map<String, String> value) {
        String result = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedis.hmset(key, value);
        } finally {
            returnResource(jedis);
        }
        return "OK".equals(result);
    }

    /**
     * 向Map中添加值， 类型为对象
     * @param key 键
     * @param value 值
     * @return
     */
    @Override
    public  boolean mapObjectPut(String key, Map<String, Object> value) throws IOException {
        String result = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            Map<byte[], byte[]> map = new HashMap<>();
            for (Map.Entry<String, Object> e : value.entrySet()){
                map.put(getBytesKey(e.getKey()), toBytes(e.getValue()));
            }
            result = jedisCommandsBytesHmset(jedis, getBytesKey(key), (Map<byte[], byte[]>)map);
        } finally {
            returnResource(jedis);
        }
        return "OK".equals(result);
    }

    /**
     * 移除Map缓存中的值
     * @param key 键
     * @param mapKey 值
     * @return
     */
    @Override
    public  long mapRemove(String key, String mapKey) {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedis.hdel(key, mapKey);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 移除Map缓存中的值
     * @param key 键
     * @param mapKey 值
     * @return
     */
    @Override
    public  long mapObjectRemove(String key, String mapKey) throws IOException {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedisCommandsBytesHdel(jedis, getBytesKey(key), getBytesKey(mapKey));
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     * @param key 键
     * @param mapKey 值
     * @return
     */
    @Override
    public  boolean mapExists(String key, String mapKey) {
        boolean result = false;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedis.hexists(key, mapKey);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 判断Map缓存中的Key是否存在
     * @param key 键
     * @param mapKey 值
     * @return
     */
    @Override
    public  boolean mapObjectExists(String key, String mapKey) throws IOException {
        boolean result = false;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedisCommandsBytesHexists(jedis, getBytesKey(key), getBytesKey(mapKey));
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    @Override
    public  long del(String key) {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis.exists(key)){
                result = jedis.del(key);
            }else{
            }
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
    @Override
    public  long delObject(String key) throws IOException {
        long result = 0;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedisCommandsBytesExists(jedis, getBytesKey(key))){
                result = jedisCommandsBytesDel(jedis, getBytesKey(key));
            }else{
            }
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    @Override
    public  boolean exists(String key) {
        boolean result = false;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedis.exists(key);
        } finally {
            returnResource(jedis);
        }
        return result;
    }

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
    @Override
    public  boolean existsObject(String key) throws IOException {
        boolean result = false;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            result = jedisCommandsBytesExists(jedis, getBytesKey(key));
        } finally {
            returnResource(jedis);
        }
        return result;

    }


    /**
     * 获取资源
     * @return
     * @throws JedisException
     */
    public  JedisCommands getResource() throws JedisException {
        JedisCommands jedis = null;
        try {
            if (jedisPool instanceof Pool) {
                jedis = (JedisCommands) ((Pool)jedisPool).getResource();
            } else if (jedisPool instanceof JedisCluster){
                jedis = (JedisCluster) jedisPool;
            }
        } catch (JedisException e) {
            returnBrokenResource(jedis);
            throw e;
        }
        return jedis;
    }

    /**
     * 归还资源
     * @param jedis
     * @param jedis
     */
    public  void returnBrokenResource(JedisCommands jedis) {
        if (jedis != null) {
            if (jedis instanceof  Jedis) {
                Jedis jedis1 = (Jedis) jedis;
                if (jedis1.isConnected()) {
                    jedis1.close();
                }
            } else if (jedis instanceof ShardedJedis) {
                ShardedJedis shardedJedis = (ShardedJedis) jedis;
                shardedJedis.close();
            } else if (jedis instanceof JedisCluster) {
                JedisCluster jedisCluster = (JedisCluster) jedis;
            }

        }
    }

    /**
     * 释放资源
     * @param jedis
     */
    public  void returnResource(JedisCommands jedis) {
        if (jedis != null ) {
            if (jedis instanceof  Jedis) {
                Jedis jedis1 = (Jedis) jedis;
                if (jedis1.isConnected()) {
                    jedis1.close();
                }
            } else if (jedis instanceof ShardedJedis) {
                ShardedJedis shardedJedis = (ShardedJedis) jedis;
                shardedJedis.close();
            } else if (jedis instanceof JedisCluster) {
                JedisCluster jedisCluster = (JedisCluster) jedis;
            }
        }
    }

    /**
     * 获取byte[]类型Key
     * @param object
     * @return
     */
    public static byte[] getBytesKey(Object object) throws IOException {
        if(object instanceof String){
            return MyStringHelper.getBytes((String)object);
        }else{
            return MyObjectHelper.serialize(object);
        }
    }

    /**
     * Object转换byte[]类型
     * @param object
     * @return
     */
    public static byte[] toBytes(Object object) throws IOException {
        return MyObjectHelper.serialize(object);
    }

    /**
     * byte[]型转换Object
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        return MyObjectHelper.unserialize(bytes);
    }

    /**
     * 获取分布式锁 返回Null表示获取锁失败
     * @version 1.0
     * @since jdk1.8
     * @date 2018/11/6
     * @param lockName 锁名称
     * @param lockTimeoutInMS 锁超时时间
     * @throws
     * @return java.lang.String 锁标识
     */
    @Override
    public String acquireLock(String lockName, long lockTimeoutInMS) {
        String retIdentifier = null;
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            String identifier = UUID.randomUUID().toString();
            String lockKey = "lock:" + lockName;
            int lockExpire = (int) (lockTimeoutInMS / 1000);
            String result = jedis.set(lockKey, identifier, "NX", "PX", lockExpire);
            if ("OK".equals(result)) {
                retIdentifier = identifier;
            }
        } finally {
            returnResource(jedis);
        }
        return retIdentifier;
    }

    /**
     * 释放锁
     * @version 1.0
     * @since jdk1.8
     * @date 2018/11/6
     * @param lockName 锁key
     * @param identifier 锁标识
     * @throws
     * @return boolean
     */
    @Override
    public boolean releaseLock(String lockName, String identifier) {
        JedisCommands conn = null;
        String lockKey = "lock:" + lockName;
        boolean retFlag = false;
        try {
            conn = getResource();
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Object result = jedisCommandsBytesLuaEvalSha(conn, script, Collections.singletonList(lockKey), Collections.singletonList(identifier));
            if ("1" == result) {
                retFlag = true;
            }

        }  finally {
            returnResource(conn);
        }
        return retFlag;
    }

    /**
     *  IP 限流
     * @version 1.0
     * @since jdk1.8
     * @date 2018/11/6
     * @param ip ip
     * @param timeout 规定时间 （秒）
     * @param limit 限制次数
     * @throws
     * @return 是否可以访问
     */
    @Override
    public boolean accessLimit(String ip, int timeout, int limit) {
        JedisCommands conn = null;
        try {
            conn = getResource();
            String lua = "local num = redis.call('incr', KEYS[1])\n" +
                    "if tonumber(num) == 1 then\n" +
                    "\tredis.call('expire', KEYS[1], ARGV[1])\n" +
                    "\treturn 1\n" +
                    "elseif tonumber(num) > tonumber(ARGV[2]) then\n" +
                    "\treturn 0\n" +
                    "else \n" +
                    "\treturn 1\n" +
                    "end\n";
            Object result = jedisCommandsBytesLuaEvalSha(conn, lua, Arrays.asList(ip), Arrays.asList(String.valueOf(timeout),
                    String.valueOf(limit)));
            return "1".equals(result == null ? null : result.toString());
        }  finally {
            returnResource(conn);
        }

    }

    private boolean jedisCommandsBytesExists(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.exists(bytesKey);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.exists(bytesKey);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.exists(bytesKey);
        }
        return false;
    }

    private byte[] jedisCommandsBytesGet(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.get(bytesKey);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.get(bytesKey);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.get(bytesKey);
        }
        return null;
    }

    private String jedisCommandsBytesSet(JedisCommands jedisCommands, byte[] bytesKey, byte[] bytesValue) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.set(bytesKey, bytesValue);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.set(bytesKey, bytesValue);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.set(bytesKey, bytesValue);
        }
        return null;
    }

    private List<byte[]> jedisCommandsBytesLrange(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.lrange(bytesKey, 0, -1);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.lrange(bytesKey, 0, -1);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.lrange(bytesKey, 0, -1);
        }
        return null;
    }

    private long jedisCommandsBytesRpush(JedisCommands jedisCommands, byte[] bytesKey, byte[][] value) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.rpush(bytesKey, value);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.rpush(bytesKey, value);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.rpush(bytesKey, value);
        }
        return 0;
    }

    private byte[] jedisCommandsBytesLpop(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.lpop(bytesKey);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.lpop(bytesKey);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.lpop(bytesKey);
        }
        return null;
    }
    private byte[] jedisCommandsBytesRpop(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.rpop(bytesKey);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.rpop(bytesKey);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.rpop(bytesKey);
        }
        return null;
    }
    private Set<byte[]> jedisCommandsBytesSmembers(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.smembers(bytesKey);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.smembers(bytesKey);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.smembers(bytesKey);
        }
        return null;
    }

    private long  jedisCommandsBytesSadd(JedisCommands jedisCommands, byte[] bytesKey, byte[][] bytesValue) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.sadd(bytesKey, bytesValue);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.sadd(bytesKey, bytesValue);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.sadd(bytesKey, bytesValue);
        }
        return 0;
    }

    private Map<byte[], byte[]> jedisCommandsBytesHgetall(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.hgetAll(bytesKey);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.hgetAll(bytesKey);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.hgetAll(bytesKey);
        }
        return null;
    }

    private String jedisCommandsBytesHmset(JedisCommands jedisCommands, byte[] bytesKey, Map<byte[], byte[]> bytesValue) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.hmset(bytesKey, bytesValue);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.hmset(bytesKey, bytesValue);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.hmset(bytesKey, bytesValue);
        }
        return null;
    }

    private long  jedisCommandsBytesHdel(JedisCommands jedisCommands, byte[] bytesKey, byte[] bytesValue) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.hdel(bytesKey, bytesValue);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.hdel(bytesKey, bytesValue);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.hdel(bytesKey, bytesValue);
        }
        return 0;
    }

    private boolean  jedisCommandsBytesHexists(JedisCommands jedisCommands, byte[] bytesKey, byte[] field) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.hexists(bytesKey, field);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.hexists(bytesKey, field);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.hexists(bytesKey, field);
        }
        return false;
    }

    private long  jedisCommandsBytesDel(JedisCommands jedisCommands, byte[] bytesKey) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.del(bytesKey);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            return shardedJedis.del(bytesKey);
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            return jedisCluster.del(bytesKey);
        }
        return 0;
    }

    private Object jedisCommandsBytesLuaEvalSha(JedisCommands jedisCommands, String lua, List key, List val) {
        if (jedisCommands instanceof Jedis) {
            Jedis jedis = (Jedis) jedisCommands;
            return jedis.evalsha(jedis.scriptLoad(lua), key, val);
        } else if (jedisCommands instanceof ShardedJedis) {
            ShardedJedis shardedJedis = (ShardedJedis) jedisCommands;
            throw new RuntimeException("ShardedJedis 暂不支持执行Lua脚本");
        } else if (jedisCommands instanceof JedisCluster) {
            JedisCluster jedisCluster = (JedisCluster) jedisCommands;
            jedisCluster.evalsha(jedisCluster.scriptLoad(lua, lua), key, val);
        }
        return 0;
    }

    @Override
    public void deleteByPattern(String pattern) throws UnsupportedEncodingException {
        JedisCommands jedis = null;
        try {
            jedis = getResource();
            if (jedis instanceof Jedis) {
                Jedis jd = (Jedis) jedis;
                Set<String> keys = jd.keys(pattern);
                if(keys != null && !keys.isEmpty()) {
                    String keyArr[] = new String[keys.size()];
                    jd.del(keys.toArray(keyArr));
                }
            }
        } finally {
            returnResource(jedis);
        }

    }


}
