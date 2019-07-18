package com.iscas.cs.server.unproxy.self.web.common;

import com.iscas.common.tools.url.UrlMatcher;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.model.ProxyServiceSetting;
import com.iscas.cs.server.proxy.util.ConfigUtils;
import com.iscas.cs.server.proxy.util.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

/**
 * 没有在代理配置内找到的处理
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 9:57
 * @since jdk1.8
 */
@Slf4j
@Component
public class UnproxyHandler {
    private UrlMatcher urlMatcher;
    public UnproxyHandler() {
        urlMatcher = new UrlMatcher();
    }
    /**
     * 没有被代理到的消息的处理,如果可以处理，返回true，不能处理返回false
     * */
    public boolean handle(FullHttpRequest request, ChannelHandlerContext ctx) {
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(
                    request.protocolVersion(),
                    HttpResponseStatus.OK);
            HttpUtils.setCrosInfos(request, response);
            ProxyServiceSetting proxyServiceSetting = ConfigUtils.readProxyServiceConfig();
            String selfWebPath = proxyServiceSetting.getSelfWebPath();
            //请求匹配到本服务页面路由
            boolean match = urlMatcher.match(selfWebPath.concat("**"), request.uri());
            if (!match) {
                return false;
            }
            log.debug("请求:{}匹配到缓存服务页面路由", request.uri());
            HttpSelfWebRouteUtils.requestToMethod(request, response, ctx);
            return true;
        } catch (Exception e) {
            log.error("获取代理配置信息出错", e);
            ErrorUtils.sendError500(request, response, e, ctx);
        }
        return false;
    }



    public static UnproxyHandler getInstance() {
        return UnproxyHandlerHolder.instance;
    }

    private static class UnproxyHandlerHolder {
        public static final UnproxyHandler instance = new UnproxyHandler();
    }
}
