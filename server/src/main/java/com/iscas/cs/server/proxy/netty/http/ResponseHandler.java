package com.iscas.cs.server.proxy.netty.http;

import com.iscas.common.redis.tools.impl.JedisClient;
import com.iscas.common.tools.core.io.zip.GzipUtils;
import com.iscas.cs.server.bean.Autowired;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.model.CacheTactics;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.util.*;
import com.iscas.cs.server.redis.RedisConn;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/3 8:49
 * @since jdk1.8
 */
@Slf4j
@Component
public class ResponseHandler {
    private JedisClient jedisClient;
    private CacheTactics cacheTactics;
    @Autowired
    private DownloadFileHandler downloadFileHandler;
    public ResponseHandler() {
        jedisClient = RedisConn.getClient();
        cacheTactics = ConfigUtils.getCacheTacticsProps();
    }



    private byte[] jsCssHtmlCompressHandle(FullHttpRequest request, byte[] bytes) {
        String requestURI = HttpUtils.getRequestUri(request);
        try {
            if (requestURI.endsWith(".js") && !requestURI.endsWith(".min.js")) {
                if (Objects.equals("1", cacheTactics.getJsCompressTactics())) {
                    return JsCssCompressUtils.jscompress(bytes, "yui");
                } else {
                    return bytes;
                }
            } else if (requestURI.endsWith(".css") &&!requestURI.endsWith(".min.css")){
                if (Objects.equals("1", cacheTactics.getCssCompressTactics())) {
                    return JsCssCompressUtils.csscompress(bytes);
                } else {
                    return bytes;
                }
            } else  {
                //认为其他请求都可以做html压缩，使用gzip，通过请求头判断
                //TODO
                String accept = HttpUtils.getHeader(request, "Accept-Encoding");
                if (StringUtils.isNotEmpty(accept) && accept.contains("gzip")) {
                    //可以压缩
                    if (Objects.equals("1", cacheTactics.getHtmlCompressTactics())) {
                        return GzipUtils.compressFromBytes(bytes);
                    } else {
                        return bytes;
                    }
                }
                return bytes;
            }
        } catch (Exception e) {
            log.warn("{}压缩失败", requestURI);
        }
        return bytes;
    }


    /**
     *
     * 返回数据时总体处理
     * */
    public void responseHandle(FullHttpRequest request, FullHttpResponse response, byte[] keyBytes, int code,
                               Map<byte[], byte[]> result, ChannelHandlerContext ctx) {
        //首先判断是否需要缓存
//        CacheTactics cacheTactics = ConfigUtils.getCacheTacticsProps();
        ProxySetting servletSetting = HttpUtils.getRouteSetting(request);
        if (servletSetting != null) {
            switch (servletSetting.getDataCache()) {
                case "-1" : {
                    //永远不需要缓存
                    break;
                }
                case "0" : {
                    //根据用户请求头判断是否要缓存
                    break;
                }
                case "1" : {
                    //所有数据都要缓存
                    cache(request,  response,false, keyBytes, result, code);
                    break;
                }
                case "2" : {
                    //只缓存静态数据
                    cache(request, response, true, keyBytes, result, code);
                    break;
                }
                default: {
                    //TODO 这里需要构建一个配置错误的返回
                    break;
                }

            }
        }


    }

    /**
     *
     * 缓存数据
     */
    public void cache(FullHttpRequest request, FullHttpResponse response, boolean staticFlag, byte[] keyBytes, Map<byte[], byte[]> responseBytesMap,
                      int statusCode) {
        //判断返回的HTTP状态码
        switch (statusCode) {
            case 200: {
                //如果是200 OK
                //如果只缓存静态资源，但此次请求不是静态资源，直接跳过
                if (staticFlag && ! HttpUtils.checkStaticResource(request)) {
                    break;
                }
                //TODO 缓存数据，这里还需要有策略，这里简单先直接缓存
                try {
                    if (responseBytesMap != null) {
                        byte[] dataKey = BytesUtils.strToBytes("data");
                        byte[] dataBytes = MapRaiseUtils.getWithBytesKey(responseBytesMap, dataKey);
                        if (dataBytes != null && dataBytes.length > 0) {
                            if (downloadFileHandler.checkFileDown(request)) {
                                //下载文件处理
                                byte[] filePathBytes = downloadFileHandler.cacheFile(dataBytes, request, response);
                                responseBytesMap.put(BytesUtils.strToBytes(Constant.DOWNFILE_CACHE_KEY), filePathBytes);
                            } else {
                                //处理JS CSS HTML压缩
                                byte[] bytes = jsCssHtmlCompressHandle(request, dataBytes);
                                MapRaiseUtils.removeWithBytesKey(responseBytesMap, dataKey);
                                responseBytesMap.put(dataKey, bytes);
                            }
                            if (MapUtils.isNotEmpty(responseBytesMap)) {
                                jedisClient.setBytesMap(keyBytes, responseBytesMap, 0);
                                log.info("请求数据被缓存");
                            } else {
                                log.warn("请求数据未缓存，获取缓存数据为空");
                                throw new RuntimeException("Request data is not cached, get cache data is empty");
                            }

                        }
                    }

                } catch (IOException e) {
                    log.error("缓存数据前，获取服务返回信息出错", e);
                    throw new RuntimeException("缓存数据前，获取服务返回信息出错", e);
                }
                break;
            }
            case 302: {
                //重定向
                //TODO 暂时直接返回
                break;
            }
            default: {
                //其他情况
                return;
            }
        }
    }

    public static ResponseHandler getInstance() {
        return ResponseHandlerHolder.responseHandler;
    }

    private static class ResponseHandlerHolder {
        public static ResponseHandler responseHandler = new ResponseHandler();
    }
}
