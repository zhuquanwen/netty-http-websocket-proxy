package com.iscas.cs.server.unproxy.self.web.entity;

import com.iscas.common.tools.url.UrlMatcher;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 10:38
 * @since jdk1.8
 */
@Getter
@Setter
public class WebInfo{
    private UrlMatcher urlMatcher = new UrlMatcher();

    /**
     * urlpath
     * */
    private String path;

    /**
     * 方法
     * */
    private Method method;

    /**
     * 请求方式
     * */
    private String requestMethod;

    /**
     * Controller class
     * */
    private Class<?> clazz;

    public WebInfo() {}

    public WebInfo(String path, String requestMethod) {
        this.path = path;
        this.requestMethod = requestMethod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebInfo webInfo = (WebInfo) o;
        String path = webInfo.getPath();
        return urlMatcher.match(this.path, path) && this.requestMethod.equals(webInfo.getRequestMethod());
    }

}
