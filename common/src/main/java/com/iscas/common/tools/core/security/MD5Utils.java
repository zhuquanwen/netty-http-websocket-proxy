package com.iscas.common.tools.core.security;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * MD5加密解密工具类
 * @author zhuquanwen
 * @date 2018/7/13
 **/

public final class MD5Utils {
    private MD5Utils(){}
    /**
     * MD5加密，不带盐
     * @date 2018/7/14
     * @param input 输入字符串
     * @throws NoSuchAlgorithmException 获取MessageDigest对象失败 {@link MessageDigest}
     * @return java.lang.String
     */
    public static String md5(String input) throws NoSuchAlgorithmException {

        assert StringUtils.isNotBlank(input);
        MessageDigest md5 = null;
        md5 = MessageDigest.getInstance("md5");
        char[] charArray = input.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        return md5(byteArray);
    }

    /**
     * MD5加密，自动生成盐
     * @date 2018/7/14
     * @param str 要加密的串
     * @return java.lang.String
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public static String saltMD5(String str) throws NoSuchAlgorithmException {
        assert StringUtils.isNotBlank(str);
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        String salt = sb.toString();
        str = md5Hex(str + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = str.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = str.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 加密校验
     * @date 2018/7/14
     * @param str 明文
     * @param md5 密文
     * @return boolean 校验成功与否
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public static boolean saltVerify(String str, String md5) throws NoSuchAlgorithmException {
        assert StringUtils.isNotBlank(str);
        assert StringUtils.isNotBlank(md5);
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        return md5Hex(str + salt).equals(new String(cs1));
    }
    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    private static String md5Hex(String src) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("md5");
        byte[] bs = md5.digest(src.getBytes());
        return new String(new Hex().encode(bs));

    }

    /**
     * MD5加密，不带盐
     * @date 2018/7/14
     * @param input 输入字符串
     * @throws NoSuchAlgorithmException 获取MessageDigest对象失败 {@link MessageDigest}
     * @return java.lang.String
     */
    public static String md5(byte[] input) throws NoSuchAlgorithmException {

        assert input != null;
        MessageDigest md5 = null;
        md5 = MessageDigest.getInstance("md5");
        byte[] md5Bytes = md5.digest(input);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

}
