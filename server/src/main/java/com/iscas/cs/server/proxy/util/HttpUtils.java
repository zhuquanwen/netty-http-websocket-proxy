package com.iscas.cs.server.proxy.util;

import cn.hutool.core.map.MapUtil;
import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.base.HttpStatusEnum;
import com.iscas.cs.server.proxy.model.ProxySetting;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/9 10:10
 * @since jdk1.8
 */
@Slf4j
public class HttpUtils {
    private HttpUtils() {}

    /**
     * 构建返回结构体
     * */
    public static void createHttpResponse(FullHttpRequest request, HttpStatusEnum httpStatusEnum, String errorMsg, ChannelHandlerContext ctx) {
        FullHttpResponse httpResponse = null;
        log.debug("正在构建返回协议");
        HttpVersion httpVersion = request.protocolVersion();
        log.debug("正在构建返回的response");
        HttpResponseStatus httpResponseStatus = new HttpResponseStatus(httpStatusEnum.getStatus(), httpStatusEnum.getMsgEn());
        httpResponse = new DefaultFullHttpResponse(
                request.protocolVersion(),
                httpResponseStatus);

        //判断请求类型
        String accept = getHeader(request, "Accept");
        String content = "";
        if (accept != null && accept.contains("text/html")) {
            //返回html
            setHeader(httpResponse, "Content-Type", "text/html;charset=utf-8");
            try {
                content = ConfigUtils.getResponseHtml();
            } catch (IOException e) {
                log.error("获取html返回类型出错", e);
            }
        } else if (accept != null && accept.contains("application/json")) {
            //返回json
            setHeader(httpResponse, "Content-Type", "application/json;charset=utf-8");
            try {
                content = ConfigUtils.getResponseJson();
            } catch (IOException e) {
                log.error("获取json返回类型出错", e);
            }
        } else if (accept != null && accept.contains("application/xml")) {
            //返回xml
            setHeader(httpResponse, "Content-Type","application/xml;charset=utf-8");
            try {
                content = ConfigUtils.getResponseXml();
            } catch (IOException e) {
                log.error("获取xml返回类型出错", e);
            }
        } else {
            //默认返回html
            setHeader(httpResponse, "Content-Type", "text/html;charset=utf-8");
            try {
                content = ConfigUtils.getResponseHtml();
            } catch (IOException e) {
                log.error("获取html返回类型出错", e);
            }
        }

        content = content.replace("@status", String.valueOf(httpStatusEnum.getStatus()));
        content = content.replace("@desc", String.valueOf(httpStatusEnum.getMsg()));
        content = content.replace("@detail", errorMsg == null ? "" : errorMsg);
        try {
//            ByteBuf byteBuf = ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, content);
            sendStrMsg(request, httpResponse, content, ctx);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ctx != null && ctx.channel().isActive()) {
                ctx.close();
            }
        }

    }

    public static void setCrosInfos(FullHttpRequest request, FullHttpResponse response) {
        HttpHeaders headers = request.headers();
        if (headers != null) {
            String origin = headers.get("origin");
            HttpHeaders responseHeades = response.headers();
            if (origin == null) {
                origin = "*";
            }
            responseHeades.set("Access-Control-Allow-Origin",origin);
            responseHeades.set("Access-Control-Allow-Credentials","true");
            responseHeades.set("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");
            responseHeades.set("Access-Control-Allow-Headers",
                    "Accept,Authorization,Cache-Control,Content-Type,DNT,If-Modified-Since,Keep-Alive,Origin,User-Agent,X-Mx-ReqToken,X-Requested-With");
        }

    }



    /**
     * 判断请求是否为静态资源
     * */
    public static boolean checkStaticResource(HttpRequest request) {
        String requestURI = request.uri();
        if (requestURI.contains("?")) {
            requestURI = StringUtils.substringBefore(requestURI, "?");
        }
        if (requestURI.endsWith(".js") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".png") ||
                requestURI.endsWith(".jpg") ||
                requestURI.endsWith(".gif") ||
                requestURI.endsWith(".ico")) {
            return true;
        }
        return false;
    }

    /**
     * 获取此请求对应的servlet配置
     * */
    public static ProxySetting getRouteSetting(FullHttpRequest req) {
        try {
            ConfigUtils.readProxyServiceConfig();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String requestURI = req.uri();
        for(Map.Entry<String, ProxySetting> entry: Constant.PROXY_SERVLET_SETTING_MAP.entrySet()) {
            String key = entry.getKey();
            ProxySetting value = entry.getValue();
            String key2 = StringUtils.substringBeforeLast(key, "/*");
            if (requestURI.startsWith(key2)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 设置contentType
     * */
    public static void setContentType(FullHttpResponse response, String conetentType) {
        HttpHeaders headers = response.headers();
        headers.set("Content-Type", conetentType);
    }


    public static void sendSuccessStrMsg(FullHttpRequest request, FullHttpResponse response,
                               String str, ChannelHandlerContext ctx) {
        response.setStatus(HttpResponseStatus.OK);
//        ByteBuf byteBuf = null;
//        try {
//            byteBuf = ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, str);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        response = response.replace(byteBuf);
//        HttpUtils.setCrosInfos(request, response);
//        ctx.writeAndFlush(response);
//        ctx.close();
        sendStrMsg(request, response, str, ctx);

    }

    public static void sendStrMsg(FullHttpRequest request, FullHttpResponse response,
                                         String str, ChannelHandlerContext ctx) {
        ByteBuf byteBuf = null;
        try {
            byteBuf = ByteBufUtil.writeUtf8(ByteBufAllocator.DEFAULT, str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response = response.replace(byteBuf);
        sendResponse(request, response, ctx);

    }

    public static void sendResponse(FullHttpRequest request, FullHttpResponse response,
                                  ChannelHandlerContext ctx) {
        HttpUtils.setCrosInfos(request, response);
        ctx.writeAndFlush(response);
        ctx.close();

    }

    public static String getRequestUri(FullHttpRequest req) {
        String uri = req.uri();
        if (uri.contains("?")) {
            uri = StringUtils.substringBefore(uri, "?");
        }
        return uri;
    }

    public static Map<String, String[]> getRequestParamMap(FullHttpRequest req) {
        Map<String, String[]> map = new HashMap<>();
        QueryStringDecoder decoder = new QueryStringDecoder(req.uri());
        decoder.parameters().entrySet().forEach( entry -> {
            // entry.getValue()是一个List, 只取第一个元素
            if (CollectionUtils.isNotEmpty(entry.getValue())) {
                String key = entry.getKey();
                List<String> value = entry.getValue();
                String[] valueStr = value.toArray(new String[0]);
                map.put(key, valueStr);
            }

        });
        return map;
    }

    public static String getRequestParam(FullHttpRequest req, String key) {
        Map<String, String[]> map = getRequestParamMap(req);
        if (MapUtils.isNotEmpty(map)) {
            String[] strings = map.get(key);
            if (!ArrayUtils.isEmpty(strings)) {
                return strings[0];
            }
        }
        return null;
    }

    public static Map<String, String> getRequestHeaderMap(FullHttpRequest req) {
        Map<String, String> map = new HashMap<>();
        HttpHeaders headers = req.headers();
        if (headers != null) {
            Set<String> names = headers.names();
            for (String name : names) {
                map.put(name, headers.get(name));
            }
        }
        return map;
    }

    public static Map<String, String> getResponseHeaderMap(FullHttpResponse res) {
        Map<String, String> map = new HashMap<>();
        HttpHeaders headers = res.headers();
        if (headers != null) {
            Set<String> names = headers.names();
            for (String name : names) {
                map.put(name, headers.get(name));
            }
        }
        return map;
    }

    public static String getHeader(FullHttpRequest req, String name) {
        String result = null;
        HttpHeaders headers = req.headers();
        if (headers != null) {
            result = headers.get(name);
        }
        return result;
    }

    public static String getHeader(FullHttpResponse res, String name) {
        String result = null;
        HttpHeaders headers = res.headers();
        if (headers != null) {
            result = headers.get(name);
        }
        return result;
    }

    public static void setHeader(FullHttpResponse res, String name, String value) {
        HttpHeaders headers = res.headers();
        if (headers != null) {
            headers.set(name, value);
        }
    }
}
