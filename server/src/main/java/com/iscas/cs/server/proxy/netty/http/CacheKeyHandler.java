package com.iscas.cs.server.proxy.netty.http;

import com.iscas.common.tools.core.security.MD5Utils;
import com.iscas.common.tools.url.UrlMatcher;
import com.iscas.cs.server.bean.Autowired;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.util.BytesUtils;
import com.iscas.cs.server.proxy.util.HttpUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * 缓存key的处理
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/17 15:37
 * @since jdk1.8
 */
@Component
@Slf4j
public class CacheKeyHandler {
    @Autowired
    private PlaybackHandler playbackHandler;

    private UrlMatcher urlMatcher;
    public CacheKeyHandler() {
        urlMatcher = new UrlMatcher();
    }

    /**
     * 获取可能被缓存的key
     * */
    public byte[] getKeyBytes(FullHttpRequest req) throws Exception {
        String prefix = "res:";
        String appKey = getAppKey(req);
        String userInfo = getUserInfo(req);

        try {
            byte[] bytes = new byte[0];
            ByteBuf content = req.content();
            if (content != null) {
                bytes = ByteBufUtil.getBytes(content);
            }
            //处理请求回放
            playbackHandler.playbackHandle(req, bytes);

            String requestURI = HttpUtils.getRequestUri(req);
            Map<String, String[]> parameterMap = HttpUtils.getRequestParamMap(req);
            //将URL 和参数拼接到一起做成字符串，然后再取其编码
            StringBuilder sb = new StringBuilder();
            sb.append(requestURI);
            if (MapUtils.isNotEmpty(parameterMap)) {
                for (Map.Entry<String, String[]> entry: parameterMap.entrySet()) {
                    String key = entry.getKey();
                    sb.append(key).append(";");
                    String[] values = entry.getValue();
                    if (values != null) {
                        for (String value : values) {
                            sb.append(value).append(";");
                        }
                    }
                }
            }
            //加上请求方式
            String requestMethod = req.method().toString();
            sb.append(";").append(requestMethod);

            byte[] bytes1 = BytesUtils.strToBytes(sb.toString());
            byte[] reqBytes = Arrays.copyOf(bytes1, bytes.length + bytes1.length);
            int j = 0;
            for (int i = bytes1.length; i < reqBytes.length ; i++) {
                reqBytes[i] = bytes[j++];
            }
            String reqStr = MD5Utils.md5(reqBytes);
            byte[] prefixBytes = BytesUtils.strToBytes(prefix);
            byte[] appKeyBytes = BytesUtils.strToBytes(appKey);
            byte[] userInfoBytes = BytesUtils.strToBytes(userInfo);
            byte[] reqStrBytes = BytesUtils.strToBytes(reqStr);
            byte[] newBytes = new byte[prefixBytes.length + appKeyBytes.length + userInfoBytes.length + reqStrBytes.length];
            int index = 0;
            for (int i = 0; i < prefixBytes.length; i++) {
                newBytes[index++] = prefixBytes[i];
            }
            for (int i = 0; i < appKeyBytes.length; i++) {
                newBytes[index++] = appKeyBytes[i];
            }
            for (int i = 0; i < userInfoBytes.length; i++) {
                newBytes[index++] = userInfoBytes[i];
            }
            for (int i = 0; i < reqStrBytes.length; i++) {
                newBytes[index++] = reqStrBytes[i];
            }
            return newBytes;

        } catch (Exception e) {
//			e.printStackTrace();
            log.error("获取请求体数据出错", e);
            throw new Exception("获取请求体数据出错", e);
        }
    }

    private String getUserInfo(FullHttpRequest request) {
        //TODO 这里需要根据权限认证策略设置
        String user = "default:";
        //静态资源共享，使用defualt用户
        if (HttpUtils.checkStaticResource(request)) {
            user = "default:";
        }
        return user;
    }

    private String getAppKey(FullHttpRequest request) {
        String url = request.uri();
        for(Map.Entry<String, ProxySetting> entry: Constant.PROXY_SERVLET_SETTING_MAP.entrySet()) {
            String key = entry.getKey();
            String pattern = key.concat("*");
            ProxySetting value = entry.getValue();
            if (urlMatcher.match(pattern, url)) {
                return key.substring(0, key.lastIndexOf("/")) + ":";
            }
        }
        return null;
    }
}
