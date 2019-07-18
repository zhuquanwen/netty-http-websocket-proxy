package com.iscas.common.redis.tools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Jedis操作接口
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/11/5 14:44
 * @since jdk1.8
 */


public interface IJedisClient {

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */

    String get(String key);

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
    Object getObject(String key) throws IOException, ClassNotFoundException;

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    boolean set(String key, String value, int cacheSeconds);

    /**
     * 设置缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    boolean setObject(String key, Object value, int cacheSeconds) throws IOException;

    /**
     * 获取List缓存
     * @param key 键
     * @return 值
     */
     List<String> getList(String key);

    /**
     * 获取List缓存
     * @param key 键
     * @return 值
     */
     List<Object> getObjectList(String key) throws IOException, ClassNotFoundException;

    /**
     * 设置List缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
     long setList(String key, List<String> value, int cacheSeconds);

    /**
     * 设置List缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
     long setObjectList(String key, List<Object> value, int cacheSeconds) throws IOException;

    /**
     * 向List缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
     long listAdd(String key, String... value);

    /**
     * 向List缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
     long listObjectAdd(String key, Object... value) throws IOException;

     /**
      * 模拟出队列
      * @param key 键
      * @return
      * */
     String lpopList(String key);

    /**
     * 模拟出队列，存储为对象
     * @param key 键
     * @return
     * */
     Object lpopObjectList(String key) throws IOException, ClassNotFoundException;

    /**
     * 模拟出栈，存储为字符串
     * @param key 键
     * @return
     * */
     String rpopList(String key);

    /**
     * 模拟出栈，存储为对象
     * @param key 键
     * @return
     * */
     Object rpopObjectList(String key) throws IOException, ClassNotFoundException;

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
     Set<String> getSet(String key);

    /**
     * 获取缓存
     * @param key 键
     * @return 值
     */
     Set<Object> getObjectSet(String key) throws IOException, ClassNotFoundException;

    /**
     * 设置Set缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
     long setSet(String key, Set<String> value, int cacheSeconds);

    /**
     * 设置Set缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
     long setObjectSet(String key, Set<Object> value, int cacheSeconds) throws IOException;

    /**
     * 向Set缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
     long setSetAdd(String key, String... value);

    /**
     * 向Set缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
     long setSetObjectAdd(String key, Object... value) throws IOException;

    /**
     * 获取Map缓存
     * @param key 键
     * @return 值
     */
     Map<String, String> getMap(String key);

    /**
     * 获取Map缓存
     * @param key 键
     * @return 值
     */
    Map<byte[], byte[]> getBytesMap(byte[] key);

    /**
     * 获取Map缓存
     * @param key 键
     * @return 值
     */
     Map<String, Object> getObjectMap(String key) throws IOException, ClassNotFoundException;

    /**
     * 设置Map缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
     boolean setMap(String key, Map<String, String> value, int cacheSeconds);

    /**
     * 设置Map缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
    boolean setBytesMap(byte[] key, Map<byte[], byte[]> value, int cacheSeconds);

    /**
     * 设置Map缓存
     * @param key 键
     * @param value 值
     * @param cacheSeconds 超时时间，0为不超时
     * @return
     */
     boolean setObjectMap(String key, Map<String, Object> value,
                          int cacheSeconds) throws IOException;

    /**
     * 向Map缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
     boolean mapPut(String key, Map<String, String> value);

    /**
     * 向Map缓存中添加值
     * @param key 键
     * @param value 值
     * @return
     */
     boolean mapObjectPut(String key, Map<String, Object> value) throws IOException;

    /**
     * 移除Map缓存中的值
     * @param key 键
     * @param mapKey 值
     * @return
     */
     long mapRemove(String key, String mapKey);

    /**
     * 移除Map缓存中的值
     * @param key 键
     * @param mapKey 值
     * @return
     */
     long mapObjectRemove(String key, String mapKey) throws IOException;

    /**
     * 判断Map缓存中的Key是否存在
     * @param key 键
     * @param mapKey 值
     * @return
     */
     boolean mapExists(String key, String mapKey);

    /**
     * 判断Map缓存中的Key是否存在
     * @param key 键
     * @param mapKey 值
     * @return
     */
     boolean mapObjectExists(String key, String mapKey) throws IOException;

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
     long del(String key);

    /**
     * 删除缓存
     * @param key 键
     * @return
     */
     long delObject(String key) throws IOException;

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
     boolean exists(String key);

    /**
     * 缓存是否存在
     * @param key 键
     * @return
     */
     boolean existsObject(String key) throws IOException;


    /**
     * 获取分布式锁
     * @param lockName 锁key
     * @param lockTimeoutInMS 锁超时时间
     * @return 锁标识
     */
    String acquireLock(String lockName, long lockTimeoutInMS);

    /**
     * 缓存是否存在
     * @param lockName 锁key
     * @param identifier 锁标识
     * @return
     */
    boolean releaseLock(String lockName, String identifier);

    boolean accessLimit(String ip, int timeout, int limit);
    void deleteByPattern(String pattern) throws UnsupportedEncodingException;
}
