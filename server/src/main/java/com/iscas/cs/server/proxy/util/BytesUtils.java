package com.iscas.cs.server.proxy.util;

import java.io.UnsupportedEncodingException;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/9 16:41
 * @since jdk1.8
 */
public class BytesUtils {
    private BytesUtils() {}

    public static byte[] strToBytes(String str, String charset) throws UnsupportedEncodingException {
        return str.getBytes(charset);
    }

    public static byte[] strToBytes(String str) throws UnsupportedEncodingException {
        return strToBytes(str, "utf-8");
    }

    public static String bytesToStr(byte[] bytes, String charset) throws UnsupportedEncodingException {
        return new String(bytes, charset);
    }

    public static String bytesToStr(byte[] bytes) throws UnsupportedEncodingException {
        return bytesToStr(bytes, "utf-8");
    }
}
