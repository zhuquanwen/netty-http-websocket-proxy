package com.iscas.cs.server.proxy.util;

import org.apache.commons.collections4.MapUtils;

import java.util.Arrays;
import java.util.Map;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/9 13:40
 * @since jdk1.8
 */
public class MapRaiseUtils {
    private MapRaiseUtils() {
    }

    public static <T> T getWithBytesKey(Map<byte[], T> map, byte[] searchKey) {
        if (MapUtils.isNotEmpty(map)) {
            for (Map.Entry<byte[], T> entry: map.entrySet()) {
                byte[] key = entry.getKey();
                T value = entry.getValue();
                if (Arrays.equals(key, searchKey)) {
                    return value;
                }
            }
        }
        return null;
    }

    public static <T> void removeWithBytesKey(Map<byte[], T> map, byte[] searchKey) {
        if (MapUtils.isNotEmpty(map)) {
            byte[] removeKey = null;
            for (Map.Entry<byte[], T> entry: map.entrySet()) {
                byte[] key = entry.getKey();
                if (Arrays.equals(key, searchKey)) {
                    removeKey = key;
                    break;
                }
            }
            if (removeKey != null) {
                map.remove(removeKey);
            }
        }

    }

}
