package com.iscas.common.tools.core.convert;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/4/18 10:28
 * @since jdk1.8
 */
public class BytesConvertUtils {
    private BytesConvertUtils() {}

    /**
     * 以大端模式将一个32位的数值转成byte[]，目前支持float、int
     */
    public static <T> byte[] _32ToBytesBig(T value) {
        byte[] src = new byte[4];
        int data = getIntData(value);
        src[0] = (byte) ((data >> 24) & 0xFF);
        src[1] = (byte) ((data >> 16) & 0xFF);
        src[2] = (byte) ((data >> 8) & 0xFF);
        src[3] = (byte) (data & 0xFF);
        return src;
    }

    /**
     * 以小端模式将32位的数值转成byte[]，目前支持float、int
     *
     * @param value
     * @return
     */
    public static <T> byte[] _32ToBytesLittle(T value) {
        int data = getIntData(value);
        byte[] src = new byte[4];
        src[3] = (byte) ((data >> 24) & 0xFF);
        src[2] = (byte) ((data >> 16) & 0xFF);
        src[1] = (byte) ((data >> 8) & 0xFF);
        src[0] = (byte) (data & 0xFF);
        return src;
    }

    /**
     * 以大端模式将byte[]转成32位
     **/
    public static <T> T bytesTo_32Big(byte[] src, int offset, Class<T> clazz) {
        int obj = 0;
        obj = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        Object o = castIntToValue(obj, clazz);
        return (T)o;
    }

    /**
     * 以小端模式将byte[]转成32位
     */
    public static <T> T bytesTo_32Little(byte[] src, int offset, Class<T> clazz) {
        int obj = 0;
        obj = (int) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8)
                | ((src[offset + 2] & 0xFF) << 16)
                | ((src[offset + 3] & 0xFF) << 24));
        Object o = castIntToValue(obj, clazz);
        return (T)o;
    }

    /**
     * 以大端模式将byte[]转成16位short
     **/
    public static short bytesToShortBig(byte[] src, int offset) {
        short obj = (short) (((src[offset] & 0xFF) << 8)
                | (src[offset] & 0xFF));
        return obj;
    }

    /**
     * 以小端模式将byte[]转成16位short
     */
    public static short bytesToShortLittle(byte[] src, int offset) {
        short obj = (short) ((src[offset] & 0xFF)
                | ((src[offset + 1] & 0xFF) << 8));
        return obj;
    }


    private static Object castIntToValue(int data, Class<?> clazz) {
        if (clazz == int.class || clazz == Integer.class) {
            return data;
        } else if (clazz == float.class || clazz == Float.class) {
            return Float.intBitsToFloat(data);
        } else {
            throw new IllegalArgumentException("数据类型不合法");
        }
    }

    private static int getIntData(Object value) {
        int data = 0;
        if (value instanceof Float) {
            float datax = (float) value;
            data = Float.floatToIntBits(datax);
        } else if (value instanceof Integer) {
            data = (int) value;
        } else {
            throw new IllegalArgumentException("参数：" + String.valueOf(value) + "不合法");
        }
        return data;
    }
}
