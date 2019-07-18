package com.iscas.cs.server.proxy.netty.service;


import com.iscas.cs.server.proxy.model.PlaybackRequest;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/5 14:22
 * @since jdk1.8
 */
public interface IPlaybackService {
    /**
     * 将需要回放的信息放入队列
     * */
    void put(PlaybackRequest playbackRequest);

    /**
     * 回放数据，key为应用标识，
     * */
    void playback(String key);

    /**
     * 回放一条
     * */
    PlaybackRequest playback(PlaybackRequest playbackRequest);

    /**
     * 放入死信队列
     * */
    void putToDeadLetter(PlaybackRequest playbackRequest);

    /**死信队列的处理*/
    void deadLetterHandle();

    /**
     * 用户通知
     * */
    void notifyToUser();
}
