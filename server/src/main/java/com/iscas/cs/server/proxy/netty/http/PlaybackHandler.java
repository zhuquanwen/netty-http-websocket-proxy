package com.iscas.cs.server.proxy.netty.http;

import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.model.CacheTactics;
import com.iscas.cs.server.proxy.model.PlaybackRequest;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.model.ServerHealth;
import com.iscas.cs.server.proxy.util.ConfigUtils;
import com.iscas.cs.server.proxy.util.HttpUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/5 15:36
 * @since jdk1.8
 */
@Component
@Slf4j
public class PlaybackHandler {
    private CacheTactics cacheTactics;
    public ThreadLocal<PlaybackRequest> backRequestLocal = new ThreadLocal<>();
    public PlaybackHandler() {
        cacheTactics = ConfigUtils.getCacheTacticsProps();
    }

    public static PlaybackHandler getInstance() {
        return PlaybackHandlerHolder.instance;
    }

    public void playbackHandle (FullHttpRequest req, byte[] bodyBytes) {

        ProxySetting servletSetting = HttpUtils.getRouteSetting(req);
        if (servletSetting != null) {
            switch (servletSetting.getDataCachePlayback()) {
                case "-1" :{
                    //不需要回放数据
                    return;
                }
                case "1": {
                    //根据应用连接断了没有判断
                    String requestURI = HttpUtils.getRequestUri(req);
                    for(Map.Entry<String, ProxySetting> entry: Constant.PROXY_SERVLET_SETTING_MAP.entrySet()) {
                        String key = entry.getKey();
                        ProxySetting value = entry.getValue();
                        String key2 = StringUtils.substringBeforeLast(key, "*");
                        if (requestURI.startsWith(key2)) {
                            //找到了对应的连接配置
                            ServerHealth serverHealth = Constant.SERVER_HEALTH_MAP.get(key);
                            if (serverHealth != null && Objects.equals("-1", serverHealth.getHealth())) {
                                //此应用连接断开了,创建一个PlaybackRequest对象，队列入队
                                PlaybackRequest playbackRequest = new PlaybackRequest();
                                String prefix = value.getUrlPrefix();
                                //拼接请求URL完全体
                                String url = prefix.concat(requestURI);
                                //构建请求参数
                                Map<String, String[]> params = HttpUtils.getRequestParamMap(req);
                                //获取请求header
                                Map<String, String> header = HttpUtils.getRequestHeaderMap(req);

                                String method = req.method().toString();
                                playbackRequest.setKey(key2)
                                        .setContent(bodyBytes)
                                        .setHeaders(header)
                                        .setParams(params)
                                        .setUrl(url)
                                        .setMethod(method)
                                        .setUserInfo("default"); //TODO 用户信息暂时用default
                                //暂时先不入队，加入threadlocal, 如果用户缓存策略为获取缓存并且获取到了缓存则后面也不入队了
                                backRequestLocal.set(playbackRequest);
//                            playbackQueService.put(playbackRequest);
                                //TODO 这里给用户直接返回数据，不要再去请求了
                            }
                            break;
                        }
                    }
                }
                case "0": {
                    //TODO 根据用户请求头进行缓存，待开发
                }
            }
        }
    }

    private static class PlaybackHandlerHolder {
        public static PlaybackHandler instance = new PlaybackHandler();
    }
}
