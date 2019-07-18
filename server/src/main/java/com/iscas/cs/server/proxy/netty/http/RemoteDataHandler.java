package com.iscas.cs.server.proxy.netty.http;

import com.iscas.common.tools.url.URLUtils;
import com.iscas.cs.server.bean.Autowired;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.okhttp.OkHttpCustomClient;
import com.iscas.cs.server.proxy.util.BytesUtils;
import com.iscas.cs.server.proxy.util.HttpUtils;
import com.iscas.cs.server.proxy.util.SerializableUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.entity.DecompressingEntity;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.tools.ant.taskdefs.condition.Http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 获取远程服务器的数据处理器
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/17 17:47
 * @since jdk1.8
 */
@Component
@Slf4j
public class RemoteDataHandler {
    @Autowired
    private ResponseHandler responseHandler;

    private OkHttpCustomClient client;
    public RemoteDataHandler () {
        client = OkHttpCustomClient.getInstance();
    }
    public boolean response(FullHttpRequest req, byte[] keyBytes, ChannelHandlerContext ctx) throws IOException {
        FullHttpResponse res = null;
        //获取header
        Map<String, String> requestHeaderMap = HttpUtils.getRequestHeaderMap(req);
        //获取请求体
        ByteBuf content = req.content();
        //获取请求uri
        String uri = req.uri();
        ProxySetting routeSetting = HttpUtils.getRouteSetting(req);

        Map<byte[], byte[]> result = new HashMap<>();


        if (routeSetting != null) {
            String targetUrl = routeSetting.getTargetUrl();
            //远程服务的地址
            URI targetUriObj = null;
            try {
                targetUriObj = new URI(targetUrl);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            HttpHost httpHost = URIUtils.extractHost(targetUriObj);
            String hostStr = httpHost.toString();
            String realTargetUrl = hostStr.concat(uri);

            //获取请求类型
            HttpMethod method = req.method();

            Response response = null;
            switch (method.toString()) {
                case "GET": {
                    response = client.doGetBody(realTargetUrl, requestHeaderMap);
                    break;
                }
                case "DELETE": {

                    response = client.doDeleteBody(realTargetUrl, requestHeaderMap);
                    break;
                }
                case "POST": {
                    response = postOrPutHandle(content, realTargetUrl, requestHeaderMap, method.toString());
                    break;
                }
                case "PUT": {
                    response = postOrPutHandle(content, realTargetUrl, requestHeaderMap, method.toString());
                    break;
                }
            }
            if (response != null) {
                HttpResponseStatus httpResponseStatus = new HttpResponseStatus(response.code(), response.message());
                res = new DefaultFullHttpResponse(req.protocolVersion(), httpResponseStatus);
                //获取返回的header
                Headers okhttpHeaders = response.headers();
                HttpHeaders headers = res.headers();
                if (okhttpHeaders != null) {
                    for (String name : okhttpHeaders.names()) {
                        headers.set(name, okhttpHeaders.get(name));
                    }
                }
                //获取返回值
                ResponseBody body = response.body();
                byte[] bytes = body.bytes();
                if (bytes != null) {
                    ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
                    res = res.replace(byteBuf);
                }
                String contentType = HttpUtils.getHeader(res, "Content-Type");
                if (contentType != null) {
                    result.put(BytesUtils.strToBytes("Content-Type"), BytesUtils.strToBytes(contentType));
                }

                String encoding = HttpUtils.getHeader(res, "encoding");
                if (encoding != null) {
                    result.put(BytesUtils.strToBytes("encoding"), BytesUtils.strToBytes(encoding));
                }
                if (bytes != null) {
                    result.put(BytesUtils.strToBytes("data"), bytes);
                }

                //缓存所有其他的header
                HashMap<String, String> responseHeaderMap = (HashMap<String, String>) HttpUtils.getResponseHeaderMap(res);
                result.put(BytesUtils.strToBytes("header"), SerializableUtils.serialize(responseHeaderMap));
            }
        }
        if (res != null) {
            //缓存数据
            responseHandler.responseHandle(req, res, keyBytes, res.status().code(), result, ctx);
            ctx.writeAndFlush(res);
            ctx.close();

            return true;
        }
        return false;
    }
    private Response postOrPutHandle(ByteBuf content, String url, Map<String, String> headers, String method) throws IOException {
        byte[] bytes = new byte[0];
        if (content != null) {
            bytes = ByteBufUtil.getBytes(content);
        }
        RequestBody body = RequestBody.create(null, bytes);
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if(headers != null){
            for (Map.Entry<String, String> entry : headers.entrySet()){
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if ("POST".equalsIgnoreCase(method)) {
            requestBuilder.post(body);
        } else if ("PUT".equalsIgnoreCase(method)) {
            requestBuilder.put(body);
        }
        Request request = requestBuilder.build();
        Call call = client.getClient().newCall(request);
        return call.execute();
    }

}
