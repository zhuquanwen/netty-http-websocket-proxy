package com.iscas.cs.server.proxy.netty.service.queue;

import java.util.List;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 8:23
 * @since jdk1.8
 */
public interface IQueueService {

    /**
     * 入队
     * @param id：消息id(时间戳+url)
     * @param data:消息内容序列化后的值
     * @param topic: 此主题对应每个应用的标识
     * @param user: 用户信息（队列tag）
     * */
    void put(String id, byte[] data, String topic, String user);

    /**
     * 出队(暂时不加用户信息)
     * @param topic: 此主题对应每个应用的标识
     * @return byte[] 消息内容序列化后的值
     * */
    byte[] get(String topic);

    /**
     * 判断队列是不是空的(暂时不加用户信息)
     * @param topic: 此主题对应每个应用的标识
     *
     * */
    boolean isEmpty(String topic);


    /**
     * 判断队列是不是空的(暂时不加用户信息)
     * @param topic: 此主题对应每个应用的标识
     * @param user: 用户信息（队列tag）
     * */
    boolean isEmpty(String topic, String user);


    /**
     * 出队
     * @param topic: 此主题对应每个应用的标识
     * @param user: 用户信息（队列tag）
     * @return byte[] 消息内容序列化后的值
     * */
    byte[] get(String topic, String user);

    /**
     * 获取所有ids
     * @param topic: 此主题对应每个应用的标识
     * @return List<String> 所有的消息ID
     * */
    List<String> getAllIds(String topic);

    /**
     * 按照消息ID获取消息
     * @param id：消息id(时间戳+url)
     * @param topic: 此主题对应每个应用的标识
     * @return byte[] 消息内容
     * */
    byte[] getById(String topic, String id);


    /**
     * 获取所有ids
     * @param topic: 此主题对应每个应用的标识
     * @param user: 用户信息（队列tag）
     * @return List<String> 所有的消息ID
     * */
    List<String> getAllIds(String topic, String user);

    /**
     * 按照消息ID获取消息
     *
     * @param topic: 此主题对应每个应用的标识
     * @param user: 用户信息（队列tag）
     * @param id：消息id(时间戳+url)
     * @return byte[] 消息内容
     * */
    byte[] getById(String topic, String user, String id);

}
