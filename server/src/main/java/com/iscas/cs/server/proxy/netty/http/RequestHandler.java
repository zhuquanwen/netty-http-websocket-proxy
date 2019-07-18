package com.iscas.cs.server.proxy.netty.http;

import com.iscas.common.redis.tools.impl.JedisClient;
import com.iscas.cs.server.bean.Autowired;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.base.HttpStatusEnum;
import com.iscas.cs.server.proxy.model.CacheTactics;
import com.iscas.cs.server.proxy.model.PlaybackRequest;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.netty.service.IPlaybackService;
import com.iscas.cs.server.proxy.netty.service.PlaybackService;
import com.iscas.cs.server.proxy.util.*;
import com.iscas.cs.server.redis.RedisConn;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.HttpResponse;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Objects;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/3 14:49
 * @since jdk1.8
 */
@Slf4j
@Component
public class RequestHandler {
    private JedisClient jedisClient;
    private IPlaybackService playbackQueService;
    private CacheTactics cacheTactics;
    @Autowired
    private DownloadFileHandler downloadFileHandler;
    @Autowired
    private PlaybackHandler playbackHandler;
    public RequestHandler() {
        jedisClient = RedisConn.getClient();
        playbackQueService = PlaybackService.getInstance();
        cacheTactics = ConfigUtils.getCacheTacticsProps();
        downloadFileHandler = DownloadFileHandler.getInstance();
    }

    /**
     *
     * 判断此请求是否从缓存拿数据
     */
    private boolean checkFromCache(FullHttpRequest request) {
        boolean check = false;
        ProxySetting servletSetting = HttpUtils.getRouteSetting(request);
        if (servletSetting != null) {
            switch (servletSetting.getGetDataCache()) {
                case "-1": {
                    //不从缓存拿
                    check = false;
                    break;
                }
                case "0": {
                    //TODO 根据用户请求头判断，暂时未true
                    check = true;
                    break;
                }
                case "1": {
                    //只从缓存拿
                    check = true;
                    break;
                }
                case "2": {
                    //只从缓存取静态资源
                    if (HttpUtils.checkStaticResource(request)) {
                        check = true;
                    } else {
                        check = false;
                    }
                    break;
                }
                case "3": {
                    //优先从缓存取，取不到直接路由到云服务器
                    check = true;
                    break;
                }
            }
        }
        return check;
    }

    /**
     *
     * 判断请求从不从缓存拿数据，如果从缓存拿数据，直接返回
     * */
    public boolean requestHandle(FullHttpRequest request, byte[] keyBytes, ChannelHandlerContext ctx) throws Exception{
        boolean flag = false;
        try {
            //判断是否需要缓存
            if (checkFromCache(request)) {
                //尝试从缓存拿数据
                log.debug("尝试从缓存读取数据");
                Map<byte[], byte[]> bytesMap = jedisClient.getBytesMap(keyBytes);
                if (MapUtils.isNotEmpty(bytesMap)) {
                    flag = true;
                    log.debug("获取到缓存数据不为空");
                    //判断请求是否为文件下载
                    if (downloadFileHandler.checkFileDown(request)) {
                        //文件下载
                        downloadFileHandler.getCacheFile(bytesMap, request, ctx);
                    } else {
                        byte[] dataBytes = MapRaiseUtils.getWithBytesKey(bytesMap, BytesUtils.strToBytes("data"));
                        byte[] contentTypeBytes =MapRaiseUtils.getWithBytesKey(bytesMap, BytesUtils.strToBytes("Content-Type"));
                        byte[] encodingBytes = MapRaiseUtils.getWithBytesKey(bytesMap, BytesUtils.strToBytes("encoding"));
                        byte[] headerBytes = MapRaiseUtils.getWithBytesKey(bytesMap, BytesUtils.strToBytes("header"));
                        //自定义封装返回的各个对象
                        log.debug("正在解析请求的协议");
                        HttpVersion httpVersion = request.protocolVersion();
                        log.debug("正在构建返回的response");
                        FullHttpResponse response = new DefaultFullHttpResponse(httpVersion, HttpResponseStatus.OK);
                        if (dataBytes != null) {
                            ByteBuf byteBuf = Unpooled.wrappedBuffer(dataBytes);
                            response = response.replace(byteBuf);
                        }
                        if (contentTypeBytes != null) {
                            String contentType = BytesUtils.bytesToStr(contentTypeBytes);
                            HttpUtils.setContentType(response, contentType);
                        }
                        if (encodingBytes != null) {
                            String encoding = BytesUtils.bytesToStr(encodingBytes);
                            HttpUtils.setHeader(response, "encoding", encoding);
                        }
                        //构建缓存的header
                        if (headerBytes != null) {
                            Map<String, String> headerMap = SerializableUtils.deserialize(headerBytes);
                            if (MapUtils.isNotEmpty(headerMap)) {
                                for (Map.Entry<String, String> entry: headerMap.entrySet()) {
                                    String key = entry.getKey();
                                    String value = entry.getValue();
                                    HttpUtils.setHeader(response, key, value);
                                }
                            }
                        }

                        //设置返回头的gzip
                        if (Objects.equals("1", cacheTactics.getHtmlCompressTactics())) {
                            HttpUtils.setHeader(response, "Content-Encoding", "gzip");
                            //使用了gzip压缩后，需要改变Content-Length的值
                            HttpUtils.setHeader(response, "Content-Length", dataBytes == null ? HttpUtils.getHeader(response, "Content-Length") : String.valueOf(dataBytes.length));
                        }
                        log.info("从缓存读取数据成功，将直接返回缓存数据");
                        ctx.writeAndFlush(response);
                        ctx.close();
                    }

                } else {
                    log.info("未从缓存获取到数据");
                    //取不到缓存数据，处理逻辑
                    ProxySetting servletSetting = HttpUtils.getRouteSetting(request);
                    if (servletSetting != null) {
                        switch (servletSetting.getGetDataCache()) {
                            case "-1": {
                                //不从缓存拿
                                break;
                            }
                            case "0": {
                                //TODO 根据用户请求头判断
                                break;
                            }
                            case "1": {
                                //只从缓存拿，那么不需要回放请求
                                //TODO 组建失败的请求提示给用户
                                flag = true;
                                break ;
                            }
                            case "2": {
                                //只从缓存取静态资源
                                break;
                            }
                            case "3": {
                                //优先从缓存取，取不到直接路由到云服务器
                                break;
                            }
                        }
                    }
                    putQueue(playbackHandler.backRequestLocal, request, ctx);
                }

            }
        } catch (UnsupportedEncodingException e) {
            log.error("编码出错", e);
//            putQueue(playbackRequestThreadLocal);
        }
        return flag;

    }



    private HttpResponse putQueue(ThreadLocal<PlaybackRequest> playbackRequestThreadLocal, FullHttpRequest request,
                                  ChannelHandlerContext ctx) {
        PlaybackRequest playbackRequest = playbackRequestThreadLocal.get();
        if (playbackRequest != null) {
            //如果当前Threadlocal中有PlaybackRequest,入队
            log.debug("数据放入回放队列");
            playbackQueService.put(playbackRequest);
            HttpUtils.createHttpResponse(request, HttpStatusEnum.REQUEST_CACHED, null, ctx);
        }
        return null;
    }
    public static RequestHandler getInstance() {
        return RequestHandlerHolder.requestHandler;
    }

    private static class RequestHandlerHolder {
        public static RequestHandler requestHandler = new RequestHandler();
    }

}
