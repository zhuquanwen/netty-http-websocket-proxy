package com.iscas.cs.server.unproxy.self.web.common;

import com.iscas.cs.server.proxy.util.HttpUtils;
import com.iscas.cs.server.proxy.util.json.JsonUtils;
import com.iscas.templet.common.ResponseEntity;
import com.iscas.templet.exception.BaseException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/12 11:21
 * @since jdk1.8
 */
public class ErrorUtils {
    private ErrorUtils() {}

    public static void sendError404(FullHttpRequest request, FullHttpResponse response, ChannelHandlerContext ctx) {
        ResponseEntity responseEntity = new ResponseEntity(404, "资源不存在");
//        response.reset();
//        response.setHeader("Content-Type", "application/json;charset=utf-8");
        sendError(request, response ,responseEntity, ctx);
    }

    public static void sendError500(FullHttpRequest request, FullHttpResponse response, Throwable throwable, ChannelHandlerContext ctx) {
        ResponseEntity responseEntity = new ResponseEntity(500, "服务器内部错误");
        if (throwable != null) {
            if (throwable instanceof BaseException) {
                BaseException baseException = (BaseException) throwable;
                String message = baseException.getMessage();
                String msgDetail = baseException.getMsgDetail();
                responseEntity.setMessage(message);
                responseEntity.setDesc(msgDetail);
            } else {
                responseEntity.setMessage(throwable.getMessage());
            }
        }
        sendError(request, response ,responseEntity, ctx);

    }

    private static void sendError(FullHttpRequest request, FullHttpResponse response,
                           ResponseEntity responseEntity, ChannelHandlerContext ctx) {
        HttpUtils.setContentType(response, "application/json;charset=utf-8");
        response.setStatus(HttpResponseStatus.NOT_FOUND);
        String responseStr = JsonUtils.toJson(responseEntity);
        ByteBuf byteBuf = null;
        try {
//            byteBuf = Unpooled.wrappedBuffer(BytesUtils.strToBytes(responseStr));
            byteBuf = ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, responseStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response = response.replace(byteBuf);
        HttpUtils.setCrosInfos(request, response);
        ctx.writeAndFlush(response);
        ctx.close();
    }
}
