package com.iscas.cs.server.proxy.netty.service.queue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 8:24
 * @since jdk1.8
 */
public class MemoryQueueService implements IQueueService {
    private static Map<String, BlockingQueue<byte[]>> playbackQueMap = new ConcurrentHashMap<>();
    private static Map<String, BlockingQueue<byte[]>> playbackDeadLetterMap = new ConcurrentHashMap<>();

    public static MemoryQueueService getInstance() {
        return MemoryQueueServiceHolder.instance;
    }

    @Override
    public void put(String id, byte[] data, String topic, String user) {
        if (!playbackQueMap.containsKey(topic)) {
            playbackQueMap.put(topic, new LinkedBlockingQueue<>());
        }
        try {
            playbackQueMap.get(topic).put(data);
        } catch (InterruptedException e) {
            throw new RuntimeException("放入内存队列失败", e);
        }
    }

    @Override
    public byte[] get(String topic) {
        BlockingQueue<byte[]> queue = playbackQueMap.get(topic);
        if (queue != null) {
            try {
                return queue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException("从内存队列获取数据失败", e);
            }
        }
        return null;
    }

    @Override
    public boolean isEmpty(String topic) {
        synchronized (topic.intern()) {
            BlockingQueue<byte[]> queue = playbackQueMap.get(topic);
            return queue == null || queue.size() == 0;
        }
    }

    @Override
    public boolean isEmpty(String topic, String user) {
        return false;
    }

    @Override
    public byte[] get(String topic, String user) {
        return new byte[0];
    }

    @Override
    public List<String> getAllIds(String topic) {
        return null;
    }

    @Override
    public byte[] getById(String topic, String id) {
        return new byte[0];
    }

    @Override
    public List<String> getAllIds(String topic, String user) {
        return null;
    }

    @Override
    public byte[] getById(String topic, String user, String id) {
        return new byte[0];
    }

    private static class MemoryQueueServiceHolder {
        public static final MemoryQueueService instance = new MemoryQueueService();
    }
}
