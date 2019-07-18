package com.iscas.cs.server.proxy.netty.websocket;

import com.iscas.cs.server.proxy.netty.websocket.client.MyWebsocketClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/17 8:35
 * @since jdk1.8
 */
public class WsConstant {
    /**
     * 将请求和handshake绑定
     * */
    public static final Map<String, WebSocketServerHandshaker> wsHandshakerMap =
            new ConcurrentHashMap<>();

    /**
     * 将请求和通道绑定
     * */
    public static final Map<String, ChannelHandlerContext> wsCtx =
            new ConcurrentHashMap<>();

    /**
     * 将ctx和handshaker
     * */
    public static final Map<ChannelHandlerContext, WebSocketServerHandshaker> ctxWs =
            new ConcurrentHashMap<>();

    /**
     * 将ctx和handshaker
     * */
    public static final Map<MyWebsocketClient, ChannelHandlerContext> wsClientCtx =
            new ConcurrentHashMap<>();

    /**
     * 将ctx和handshaker
     * */
    public static final Map<ChannelHandlerContext, MyWebsocketClient> wsCtxClient =
            new ConcurrentHashMap<>();
}
