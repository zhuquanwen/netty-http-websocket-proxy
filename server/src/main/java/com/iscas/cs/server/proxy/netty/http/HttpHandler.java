package com.iscas.cs.server.proxy.netty.http;

import com.iscas.common.tools.exception.ExceptionUtils;
import com.iscas.cs.server.bean.Autowired;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.base.HttpStatusEnum;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.netty.websocket.WsHandler;
import com.iscas.cs.server.proxy.util.HttpUtils;
import com.iscas.cs.server.unproxy.self.web.common.UnproxyHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/17 9:15
 * @since jdk1.8
 */
@Component
@Slf4j
public class HttpHandler {

    @Autowired
    private WsHandler wsHandler;
    @Autowired
    private CacheKeyHandler cacheKeyHandler;
    @Autowired
    private RequestHandler requestHandler;
    @Autowired
    private PlaybackHandler playbackHandler;
    @Autowired
    private ResponseHandler responseHandler;
    @Autowired
    private RemoteDataHandler remoteDataHandler;

    // 处理HTTP的代码
    public void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) throws UnsupportedEncodingException {
        log.debug("handleHttpRequest method==========" + req.method());
        log.debug("handleHttpRequest uri==========" + req.uri());
        // 如果HTTP解码失败，返回HHTP异常
        Map<String, String> parmMap = new HashMap<>();
        if (req instanceof HttpRequest) {
            //处理本身服务的URL
            ProxySetting servletSetting = HttpUtils.getRouteSetting(req);
            if (servletSetting == null) {
                boolean handle = UnproxyHandler.getInstance().handle(req, ctx);
                if (handle) {
                    //如果此服务能够处理，直接return
                    return;
                } else {
                    //TODO 如果不能处理，如果CND代理是个重定向的话，再重定向回去，从refer取
                    return;
                }
            }
            HttpMethod method = req.method();
            log.debug("进入代理http连接");
            // 如果是websocket请求就握手升级
            if (!wsHandler.wsUpgradle(req, ctx)) {
                try {
                    log.debug("请求URL:{}", req.uri());
                    byte[] keyBytes = null;
                    try {
                        log.debug("获取请求生成的缓存key");
                        keyBytes = cacheKeyHandler.getKeyBytes(req);

                    } catch (Exception e) {
                        log.error("获取缓存Key出错", e);
                        throw new RuntimeException("获取缓存Key出错", e);
                    }
                    boolean flag = requestHandler.requestHandle(req, keyBytes, ctx);
                    if (!flag) {
                        boolean resultFlag = remoteDataHandler.response(req, keyBytes, ctx);
                        if (!flag) {
                            throw new RuntimeException("访问远程服务器出错");
                        }

                    }
                } catch (Exception e) {
                    String exceptionInfo = ExceptionUtils.getExceptionInfo(e);
                    HttpUtils.createHttpResponse(req, HttpStatusEnum.CACHE_SERVER_ERROR, exceptionInfo, ctx);
                } finally {
                    if (playbackHandler.backRequestLocal != null) {
                        playbackHandler.backRequestLocal.remove();
                    }
                }

            }

        }
    }
}
