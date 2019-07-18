package com.iscas.common.tools.url;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * url解析工具类
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2018/10/26 17:13
 * @since jdk1.8
 */
public class URLUtils {
    private URLUtils(){}

//     System.out.println("URL 是 " + url.toString());
//        System.out.println("协议是 " + url.getProtocol());
//        System.out.println("文件名是 " + url.getFile());
//        System.out.println("主机是 " + url.getHost());
//        System.out.println("路径是 " + url.getPath());
//        System.out.println("端口号是 " + url.getPort());
//        System.out.println("默认端口号是 "
//                + url.getDefaultPort());
//


    /**
     * 解析URL 协议
     * @version 1.0
     * @since jdk1.8
     * @date 2018/10/26
     * @param address 网址
     * @throws
     * @return java.lang.String
     */
    public static String getProtocol(String address) throws MalformedURLException {
        URL url = new URL(address);
        return url.getProtocol();
    }

    /**
     * 解析URL 的文件名，没有返回空字符串
     * @version 1.0
     * @since jdk1.8
     * @date 2018/10/26
     * @param address 网址
     * @throws
     * @return java.lang.String
     */
    public static String getFile(String address) throws MalformedURLException {
        URL url = new URL(address);
        return url.getFile();
    }

    /**
     * 解析URL 的主机名/域名，没有返回空字符串
     * @version 1.0
     * @since jdk1.8
     * @date 2018/10/26
     * @param address 网址
     * @throws
     * @return java.lang.String
     */
    public static String getHost(String address) throws MalformedURLException {
        URL url = new URL(address);
        return url.getHost();
    }

    /**
     * 解析URL 的路径，没有返回空字符串
     * @version 1.0
     * @since jdk1.8
     * @date 2018/10/26
     * @param address 网址
     * @throws
     * @return java.lang.String
     */
    public static String getPath(String address) throws MalformedURLException {
        URL url = new URL(address);
        return url.getPath();
    }

    /**
     * 解析URL 的端口,一般网址内没有端口都会使用默认端口会返回-1
     * @version 1.0
     * @since jdk1.8
     * @date 2018/10/26
     * @param address 网址
     * @throws
     * @return java.lang.String
     */
    public static int getPort(String address) throws MalformedURLException {
        URL url = new URL(address);
        return url.getPort();
    }

    /**
     * 解析URL 的默认端口，没有返回-1
     * @version 1.0
     * @since jdk1.8
     * @date 2018/10/26
     * @param address 网址
     * @throws
     * @return java.lang.String
     */
    public static int getDefaultPort(String address) throws MalformedURLException {
        URL url = new URL(address);
        return url.getDefaultPort();
    }

    /**
     *
     * 获取URL中的协议IP端口  或者 协议域名端口
     */
    public static String prefixUrl(String url) throws MalformedURLException {
        assert url != null;
        String protocol = getProtocol(url);
        String host = getHost(url);
        int port = getPort(url);
        if (port == -1) {
            return protocol.concat("://").concat(host);
        } else {
            return protocol.concat("://").concat(host).concat(":").concat(String.valueOf(port));
        }

    }

    public static String contactUrlForGet(String url, Map<String, String[]> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (MapUtils.isNotEmpty(params)) {
            sb.append("?");
            for(Map.Entry<String, String[]> entry: params.entrySet()) {
                String key = entry.getKey();
                String[] value = entry.getValue();
                if (value != null && value.length > 0) {
                    sb.append(key).append("=").append(value[0]).append("&");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
