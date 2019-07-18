package com.iscas.cs.server.proxy.netty.service;

import cn.hutool.core.io.IoUtil;
import com.iscas.common.tools.core.date.DateSafeUtils;
import com.iscas.common.tools.core.string.StringRaiseUtils;
import com.iscas.common.tools.url.URLUtils;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.model.CacheTactics;
import com.iscas.cs.server.proxy.model.PlaybackRequest;
import com.iscas.cs.server.proxy.netty.service.queue.IQueueService;
import com.iscas.cs.server.proxy.netty.service.queue.MemoryQueueService;
import com.iscas.cs.server.proxy.netty.service.queue.RocketMqService;
import com.iscas.cs.server.proxy.okhttp.OkHttpCustomClient;
import com.iscas.cs.server.proxy.util.BytesUtils;
import com.iscas.cs.server.proxy.util.ConfigUtils;
import com.iscas.cs.server.proxy.util.IdGenerator;
import com.iscas.cs.server.proxy.util.SerializableUtils;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 *
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/5 14:21
 * @since jdk1.8
 */
@Slf4j
@Component
public class PlaybackService implements IPlaybackService {


    private IQueueService queueService;
    private OkHttpCustomClient httpClient;
    private CacheTactics cacheTactics;
    public PlaybackService() {
        httpClient = OkHttpCustomClient.getInstance();
        queueService = MemoryQueueService.getInstance();
//        queueService = getRocketMqInstance();
        cacheTactics = ConfigUtils.getCacheTacticsProps();
    }

    private RocketMqService getRocketMqInstance() {
        if (queueService == null) {
            synchronized (RocketMqService.class) {
                if (queueService == null) {
                    queueService = new RocketMqService();
                }
            }
        }
        return (RocketMqService) queueService;
    }

    public static PlaybackService getInstance() {
        return MemoryPlaybackQueServiceHolder.instance;
    }

    @Override
    public void put(PlaybackRequest playbackRequest) {
        String id = IdGenerator.getQueueId(playbackRequest.getUrl());
        String user = playbackRequest.getUserInfo();
        String topic = playbackRequest.getKey();
        topic = StringRaiseUtils.deleteAllString(topic, "*");
        topic = StringRaiseUtils.deleteAllString(topic, "/");
        try {
            byte[] data = SerializableUtils.serialize(playbackRequest);

            //如果大小超过阈值，放入本地磁盘,把存储路径作为key替换请求体
            if (data.length > cacheTactics.getPlaybackThresholdByteSize()) {
                File parentFile = getPlaybackParentPath(topic);
                String fileName = UUID.randomUUID().toString();
                File storeFile = new File(parentFile, fileName);
                @Cleanup OutputStream os = new FileOutputStream(storeFile);
                @Cleanup ByteArrayInputStream bais = new ByteArrayInputStream(data);
                IoUtil.copy(bais, os);
                String storePath = storeFile.getAbsolutePath();
                //修改存储的内容，将inner标记为false
                playbackRequest.setContent(BytesUtils.strToBytes(storePath));
                playbackRequest.setInner(false);
                data = SerializableUtils.serialize(playbackRequest);
            }
            queueService.put(id, data , topic, user);
        } catch (IOException e) {
            log.warn("向:{}的服务队列放入需要回放的数据失败", playbackRequest.getKey());
        }

    }

    private File getPlaybackParentPath(String key) {
        String playbackPath = cacheTactics.getPlaybackPath();
        if (playbackPath == null) {
            throw new RuntimeException("In the configuration file: cache-tactics.properties configuration 'playback-path' cannot be empty");
        }
        File file = new File(playbackPath);
        //双重校验锁并发时提升效率
        if (!file.exists()) {
            synchronized (this) {
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        File keyFile = new File(file, key);
        //双重校验锁并发时提升效率
        if (!keyFile.exists()) {
            synchronized (this) {
                if (!keyFile.exists()) {
                    keyFile.mkdirs();
                }
            }
        }
        File resultFile = new File(keyFile, DateSafeUtils.format(new Date(), "yyyy-MM-dd"));
        //双重校验锁并发时提升效率
        if (!resultFile.exists()) {
            synchronized (this) {
                if (!resultFile.exists()) {
                    resultFile.mkdirs();
                }
            }
        }
        return resultFile;
    }

    @Override
    public PlaybackRequest playback(PlaybackRequest playbackRequest) {
        try {
            byte[] content = playbackRequest.getContent();
            if (!playbackRequest.isInner()) {
                //不在队里内就在本地磁盘内
                String storePath = BytesUtils.bytesToStr(content);
                @Cleanup InputStream is = new FileInputStream(storePath);
                @Cleanup ByteArrayOutputStream baos = new ByteArrayOutputStream();
                IoUtil.copy(is, baos);
                byte[] data = baos.toByteArray();
                playbackRequest = SerializableUtils.deserialize(data);
            }
            Map<String, String> headers = playbackRequest.getHeaders();
            Map<String, String[]> params = playbackRequest.getParams();
            String method = playbackRequest.getMethod();
            String url = playbackRequest.getUrl();
            content = playbackRequest.getContent();
            Response response = null;
            switch (method) {

                case "GET": {
                    String targetUrl = URLUtils.contactUrlForGet(url, params);
                    response = httpClient.doGetBody(targetUrl, headers);
                    break;
                }
                case "DELETE": {
                    String targetUrl = URLUtils.contactUrlForGet(url, params);
                    response = httpClient.doDeleteBody(targetUrl, headers);
                    break;
                }
                case "POST": {
                    String targetUrl = URLUtils.contactUrlForGet(url, params);
                    response = postOrPutHandle(content, targetUrl, headers, method);
                    break;
                }
                case "PUT": {
                    String targetUrl = URLUtils.contactUrlForGet(url, params);
                    response = postOrPutHandle(content, targetUrl, headers, method);
                    break;
                }

            }
            if (response != null && response.isSuccessful()) {
                return null;
            }
            return playbackRequest;
        } catch (Exception e) {
            return playbackRequest;
        }
    }

    @Override
    public void playback(String key) {
        key = StringRaiseUtils.deleteAllString(key, "*");
        key = StringRaiseUtils.deleteAllString(key, "/");
        while (!queueService.isEmpty(key)) {
            PlaybackRequest playbackRequest = null;
            try {
                byte[] bytes = queueService.get(key);
                playbackRequest = SerializableUtils.deserialize(bytes);
                playbackRequest = playback(playbackRequest);
                if (playbackRequest != null) {
                    putToDeadLetter(playbackRequest);
                } else {
                    //TODO 成功后用户通知
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.warn("向:{}的服务回放数据失败", key);
                putToDeadLetter(playbackRequest);
            }
        }
    }

    private Response postOrPutHandle(byte[] content, String url, Map<String, String> headers, String method) throws IOException {
        RequestBody body = RequestBody.create(null, content);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if(headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()){
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if ("POST".equalsIgnoreCase(method)) {
            requestBuilder.post(body);
        } else if ("PUT".equalsIgnoreCase(method)) {
            requestBuilder.put(body);
        }
        Request request = requestBuilder.build();
        Call call = httpClient.getClient().newCall(request);
        return call.execute();
    }

    @Override
    public void putToDeadLetter(PlaybackRequest playbackRequest) {
//        if (!playbackDeadLetterMap.containsKey(playbackRequest.getKey())) {
//            playbackDeadLetterMap.put(playbackRequest.getKey(), new LinkedBlockingQueue<>());
//        }
//        try {
//            playbackDeadLetterMap.get(playbackRequest.getKey()).put(playbackRequest);
//            log.debug("向:{}的服务死信队列放入需要回放的数据", playbackRequest.getKey());
//        } catch (InterruptedException e) {
//            log.warn("向:{}的服务死信队列放入需要回放的数据失败", playbackRequest.getKey());
//        }

        String id = IdGenerator.getQueueId(playbackRequest.getUrl());
        String user = playbackRequest.getUserInfo();
        String topic = playbackRequest.getKey();
        topic = StringRaiseUtils.deleteAllString(topic, "*");
        topic = StringRaiseUtils.deleteAllString(topic, "/");
        topic = topic.concat("_deadLetter");
        try {
            byte[] data = SerializableUtils.serialize(playbackRequest);
            queueService.put(id, data, topic, user);
        } catch (IOException e) {
            log.warn("向:{}的服务死信队列放入需要回放的数据失败", playbackRequest.getKey());
        }

    }

    @Override
    public void deadLetterHandle() {
        //TODO
    }

    @Override
    public void notifyToUser() {
        //TODO
    }

    static class MemoryPlaybackQueServiceHolder {
        public static final PlaybackService instance = new PlaybackService();
    }
}
