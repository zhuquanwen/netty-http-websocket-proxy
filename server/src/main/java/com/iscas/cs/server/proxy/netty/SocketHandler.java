package com.iscas.cs.server.proxy.netty;

import com.iscas.cs.server.bean.Autowired;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.netty.http.HttpHandler;
import com.iscas.cs.server.proxy.netty.websocket.WsConstant;
import com.iscas.cs.server.proxy.netty.websocket.WsHandler;
import com.iscas.cs.server.unproxy.self.web.common.ErrorUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/16 22:28
 * @since jdk1.8
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class SocketHandler extends BaseHttpHandler {
    @Autowired
    private WsHandler wsHandler;
    @Autowired
    private HttpHandler httpHandler;
//    private WebSocketServerHandshaker handshaker;
//    private final String wsUri = "/demo/websocket";
    /*
     * channelAction
     *
     * channel 通道 action 活跃的
     *
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     *
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug(ctx.channel().localAddress().toString() + " 通道已激活！");
    }
    /*
     * channelInactive
     *
     * channel 通道 Inactive 不活跃的
     *
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().localAddress().toString() + " 通道不活跃！");
        // 关闭流
    }
    private String getMessage(ByteBuf buf) {
        byte[] con = new byte[buf.readableBytes()];
        buf.readBytes(con);
        try {
            return new String(con, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {// 如果是HTTP请求，进行HTTP操作
            log.debug("进入http处理");
            httpHandler.handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {// 如果是Websocket请求，则进行websocket操作
            log.debug("进入websocket处理");
            wsHandler.handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }



    // 握手请求不成功时返回的应答
    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // 返回应答给客户端
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        // 如果是非Keep-Alive，关闭连接
        ChannelFuture f = ctx.channel().writeAndFlush(res);
    }

    /**
     * 功能：服务端发生异常的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出现异常", cause);
        //TODO
        ctx.close();


//        System.out.println("异常信息：\r\n" + cause.getMessage());
    }
}

