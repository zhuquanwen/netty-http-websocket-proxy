package com.iscas.cs.server.proxy.netty.websocket.client;

import com.iscas.cs.server.proxy.netty.websocket.WsConstant;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/17 9:33
 * @since jdk1.8
 */


@Slf4j
public class MyWebsocketClient extends WebSocketClient {
    private ChannelHandlerContext ctx;

    public MyWebsocketClient(String uri, ChannelHandlerContext ctx) throws URISyntaxException {
        super(new URI(uri));
        this.ctx = ctx;

    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.info("------ WebSocket onOpen ------");
//        WsConstant.wsClientCtx.put(this, ctx);
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        log.info("------ WebSocket onClose ------");
//        if (ctx.channel().isActive()) {
//            ctx.channel().close();
//        }

    }

    @Override
    public void onError(Exception arg0) {
        log.info("------ WebSocket onError ------");
//        if (ctx.channel().isActive()) {
//            ctx.channel().close();
//        }
    }

    @Override
    public void onMessage(String arg0) {
        log.info("-------- 接收到服务端数据： " + arg0 + "--------");
        if (ctx != null && ctx.channel() != null && ctx.channel().isActive()) {
            ctx.writeAndFlush(new TextWebSocketFrame(arg0));
        } else {
            //TODO 暂时这样，不知道处理的对不对
            WsConstant.ctxWs.get(ctx).close(ctx.channel(), new CloseWebSocketFrame());

        }
    }

//    public static void main(String[] args) {
//        try {
//            MyWebsocketClient myWebsocketClient = new MyWebsocketClient("http://192.168.100.88:7601/demo/websocket", null);
//            myWebsocketClient.connect();
//            while(!myWebsocketClient.getReadyState().equals(ReadyState.OPEN)){
//                System.out.println("还没有打开");
////                myWebsocketClient.close();
//            }
//            System.out.println("打开了");
//            myWebsocketClient.send("111111");
//            Thread.currentThread().join();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
}