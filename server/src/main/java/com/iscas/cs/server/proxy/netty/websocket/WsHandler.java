package com.iscas.cs.server.proxy.netty.websocket;

import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.netty.websocket.client.MyWebsocketClient;
import com.iscas.cs.server.proxy.util.HttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.utils.URIUtils;
import org.java_websocket.enums.ReadyState;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Websocket请求处理器
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/17 8:40
 * @since jdk1.8
 */
@Component
@Slf4j
public class WsHandler {
    public WsHandler() {

    }

    /**
     * 处理http请求为websocket握手时升级为websocket
     * */
    public boolean wsUpgradle(FullHttpRequest req, ChannelHandlerContext ctx) {
        boolean flag = false;
        HttpHeaders headers = req.headers();
        if (headers == null) {
            return flag;
        }
        boolean wsFlag = false;
        String connection = headers.get("Connection");
        String upgrade = headers.get("Upgrade");
        if (Objects.equals("Upgrade", connection) &&
                Objects.equals("websocket", upgrade)) {
            wsFlag = true;
        }
        if (wsFlag) {
            log.debug("websocket 请求接入");
            WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                    req.uri(), null, false);
            WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
            if (handshaker == null) {
                flag = false;
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            } else {
                //与远程的websocket建立连接
                boolean b = connectToRemoteWs(req, ctx);
                if (b) {
                    //本机websocket建联
                    handshaker.handshake(ctx.channel(), req);
                    WsConstant.wsHandshakerMap.put(req.uri(), handshaker);
                    WsConstant.wsCtx.put(req.uri(), ctx);
                    WsConstant.ctxWs.put(ctx, handshaker);
                    flag = true;
                } else {
                    //TODO 暂时先返回这样的错误提示
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                }

            }
        }
        return flag;
    }

    // 处理Websocket的代码
    public void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否是关闭链路的指令
        log.debug("接收到websocket信息");
        if (frame instanceof CloseWebSocketFrame) {
            //先关闭远程websocket的连接
            MyWebsocketClient myWebsocketClient = WsConstant.wsCtxClient.get(ctx);
            if (myWebsocketClient != null) {
                myWebsocketClient.close();
            }
            WsConstant.ctxWs.get(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否是Ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            MyWebsocketClient myWebsocketClient = WsConstant.wsCtxClient.get(ctx);
            if (myWebsocketClient != null) {
                myWebsocketClient.sendPing();
            } else {
                WsConstant.ctxWs.get(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
            return;
        }
        // 文本消息，不支持二进制消息
        if (frame instanceof TextWebSocketFrame) {
            // 返回应答消息
            String request = ((TextWebSocketFrame) frame).text();
            MyWebsocketClient myWebsocketClient = WsConstant.wsCtxClient.get(ctx);
            if (myWebsocketClient != null) {
                myWebsocketClient.send(request);
            } else {
                WsConstant.ctxWs.get(ctx).close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            }
//            ctx.channel().writeAndFlush(new TextWebSocketFrame(
//                    request + " , 欢迎使用Netty WebSocket服务，现在时刻：" + new java.util.Date().toString()));
            return;
        }

    }

    /**
     * 与远程websocket建立连接
     * */

    public boolean connectToRemoteWs(FullHttpRequest req, ChannelHandlerContext ctx) {
        boolean flag = false;
        ProxySetting servletSetting = HttpUtils.getRouteSetting(req);
        String targetUrl = servletSetting.getTargetUrl();
        //远程websocket的地址
        URI targetUriObj = null;
        try {
            targetUriObj = new URI(targetUrl);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpHost httpHost = URIUtils.extractHost(targetUriObj);
        String hostStr = httpHost.toString();
        String websocketStr = hostStr.concat(req.uri());

        try {
            MyWebsocketClient myWebsocketClient = new MyWebsocketClient(websocketStr, ctx);
            myWebsocketClient.connect();
            for (int i = 0; i < 10 ; i++) {
                if (myWebsocketClient.getReadyState().equals(ReadyState.OPEN)) {
                    flag = true;
                    WsConstant.wsClientCtx.put(myWebsocketClient, ctx);
                    WsConstant.wsCtxClient.put(ctx, myWebsocketClient);
                    break;
                }
                TimeUnit.MILLISECONDS.sleep(200);
            }
            if (!flag) {
                myWebsocketClient.close();
            }
        } catch (URISyntaxException | InterruptedException e) {
            e.printStackTrace();
        }

        return flag;
    }

}
