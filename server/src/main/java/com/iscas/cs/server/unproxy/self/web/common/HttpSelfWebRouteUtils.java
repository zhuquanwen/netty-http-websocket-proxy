package com.iscas.cs.server.unproxy.self.web.common;

import com.iscas.cs.server.proxy.util.BytesUtils;
import com.iscas.cs.server.proxy.util.HttpUtils;
import com.iscas.cs.server.proxy.util.json.JsonUtils;
import com.iscas.cs.server.unproxy.self.web.annotation.RequestBody;
import com.iscas.cs.server.unproxy.self.web.annotation.RequestParam;
import com.iscas.cs.server.unproxy.self.web.entity.WebInfo;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 14:21
 * @since jdk1.8
 */
public class HttpSelfWebRouteUtils {
    private HttpSelfWebRouteUtils() {
    }

    /**
     * 将servlet请求路由到对应的函数上
     */
    public static void requestToMethod(FullHttpRequest request, FullHttpResponse response, ChannelHandlerContext ctx) {
        String requestURI = request.uri();
        if (requestURI.contains("?")) {
            requestURI = StringUtils.substringBefore(requestURI, "?");
        }
        HttpMethod method1 = request.method();
        String requestMethod = method1.toString();
        WebInfo webInfo = new WebInfo(requestURI, requestMethod);

        //找到这个路由，执行对应的方法
        WebInfo realWebInfo = null;
        for (int index = 0; index < WebBinding.WEB_INFOS.size(); index++) {
            if (WebBinding.WEB_INFOS.get(index).equals(webInfo)) {
                realWebInfo = WebBinding.WEB_INFOS.get(index);
            }
        }

        if (realWebInfo != null) {
            //有这个路由
            webInfo = realWebInfo;
            Method method = webInfo.getMethod();
            Class<?> aClass = webInfo.getClazz();
            Object controllerObj = null;
            Object result = null;
            synchronized (aClass) {
                if (!WebBinding.CONTROLLER_MAP.containsKey(aClass)) {
                    try {
                        WebBinding.CONTROLLER_MAP.put(aClass, aClass.newInstance());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        ErrorUtils.sendError500(request, response, e, ctx);
                        return;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        ErrorUtils.sendError500(request, response, e, ctx);
                        return;
                    }
                }
                controllerObj = WebBinding.CONTROLLER_MAP.get(aClass);
            }
            Parameter[] parameters = method.getParameters();
            Object[] params = null;
            if (parameters != null && parameters.length > 0) {
                params = new Object[parameters.length];
                for (int j = 0; j < parameters.length; j++) {
                    Parameter parameter = parameters[j];
                    Class<?> type = parameter.getType();
                    if (type == FullHttpRequest.class) {
                        params[j] = request;
                    } else if (type == FullHttpResponse.class) {
                        params[j] = response;
                    } else {
                        try {
                            params[j] = null;
                            RequestBody requestBody = parameter.getAnnotation(RequestBody.class);
                            if (requestBody != null) {
                                ByteBuf byteBuf = request.content();
                                byte[] bytes = ByteBufUtil.getBytes(byteBuf);
                                //只处理JSON
                                String body = new String(bytes, "utf-8");
                                if (StringUtils.isNotEmpty(body)) {
                                    if (body.startsWith("{")) {
                                        if (Map.class.isAssignableFrom(parameter.getType())) {
                                            params[j] = JsonUtils.fromJson(body, Map.class);
                                        } else if (String.class.isAssignableFrom(parameter.getType())) {
                                            params[j] = body;
                                        } else {
                                            params[j] = JsonUtils.fromJson(body, parameter.getType());
                                        }
                                    } else if (body.startsWith("[")) {
                                        if (Collection.class.isAssignableFrom(parameter.getType())) {
                                            ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) parameter.getParameterizedType();
                                            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                                            //暂时先这么解析
                                            if (actualTypeArguments != null && actualTypeArguments.length > 0) {
                                                Collection realCollection = new ArrayList();
                                                Collection collection = JsonUtils.fromJson(body, Collection.class);
                                                Iterator iterator = collection.iterator();
                                                while (iterator.hasNext()) {
                                                    Object next = iterator.next();
                                                    String s = JsonUtils.toJson(next);
                                                    Object obj = JsonUtils.fromJson(s, (Class) actualTypeArguments[0]);
                                                    ((ArrayList) realCollection).add(obj);
                                                }
                                                params[j] = realCollection;
                                            }

                                        } else if (String.class.isAssignableFrom(parameter.getType())) {
                                            params[j] = body;
                                        } else {
                                            ErrorUtils.sendError500(request, response, new RuntimeException("请求数据解析出错"), ctx);
                                        }
                                    }
                                }
                            } else {
                                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                                if (requestParam != null) {
                                    String key = requestParam.value();
                                    if (key == null) {
                                        key = parameter.getName();
                                    }

                                    String value = null;
                                    QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
                                    Map<String, List<String>> parameterMap = decoder.parameters();
                                    if (parameterMap != null) {
                                        List<String> strings = parameterMap.get(key);
                                        if (CollectionUtils.isNotEmpty(strings)) {
                                            value = strings.get(0);
                                        }
                                    }
                                    if (int.class.isAssignableFrom(parameter.getType())) {
                                        params[j] = Integer.valueOf(value);
                                    } else if (Integer.class.isAssignableFrom(parameter.getType())) {
                                        params[j] = Integer.valueOf(value);
                                    } else if (Float.class.isAssignableFrom(parameter.getType())) {
                                        params[j] = Float.valueOf(value);
                                    } else if (float.class.isAssignableFrom(parameter.getType())) {
                                        params[j] = Float.valueOf(value);
                                    } else if (Double.class.isAssignableFrom(parameter.getType())) {
                                        params[j] = Double.valueOf(value);
                                    } else if (double.class.isAssignableFrom(parameter.getType())) {
                                        params[j] = Double.valueOf(value);
                                    } else if (String.class.isAssignableFrom(parameter.getType())) {
                                        params[j] = value;
                                    } else {
//                                        try {
//                                            response.sendError(500, "不支持的参数类型");
//                                        } catch (IOException e1) {
//                                            e1.printStackTrace();
//                                        }
                                        ErrorUtils.sendError500(request, response, new RuntimeException("不支持的参数类型"), ctx);
                                        return;
                                    }
                                } else {
                                    ErrorUtils.sendError500(request, response, new RuntimeException("不支持的参数类型"), ctx);
                                    return;

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            ErrorUtils.sendError500(request, response, e, ctx);
                            return;
                        }

                    }
                }
                try {
                    result = method.invoke(controllerObj, params);
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorUtils.sendError500(request, response, e, ctx);
                    return;
                }
            } else {
                try {
                    result = method.invoke(controllerObj);
                } catch (Exception e) {
                    e.printStackTrace();
                    ErrorUtils.sendError500(request, response, e, ctx);
                    return;
                }
            }
            //构建返回
            HttpUtils.setContentType(response, "application/json;charset=UTF-8");
            String resultStr = null;
            if (result != null && !(result instanceof String)) {
                resultStr = JsonUtils.toJson(result);
            } else {
                resultStr = result == null ? null : result.toString();
            }
            HttpUtils.sendSuccessStrMsg(request, response, resultStr, ctx);
        } else {
            ErrorUtils.sendError404(request, response, ctx);
        }


    }
}
