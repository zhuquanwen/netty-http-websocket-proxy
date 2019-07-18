package com.iscas.cs.server.proxy.util;

import lombok.Cleanup;

import java.io.*;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/11 8:47
 * @since jdk1.8
 */
public class SerializableUtils {
    private SerializableUtils() {}

    public static byte[] serialize(Serializable obj) throws IOException {
        //序列化字节流
        @Cleanup ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //对象读取
        @Cleanup ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(obj);
        //转换成字节
        return byteArrayOutputStream.toByteArray();
    }

    public static <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        //反序列化
        //直接读取直接，用对象输入流直接读取出来
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        //读取后转成对应对象
        return (T)objectInputStream.readObject();
    }
}
