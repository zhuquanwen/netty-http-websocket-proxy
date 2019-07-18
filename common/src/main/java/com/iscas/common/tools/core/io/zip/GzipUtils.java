package com.iscas.common.tools.core.io.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/12 14:56
 * @since jdk1.8
 */
public class GzipUtils {
    private GzipUtils() {}

    public static byte[] compress(String str) throws IOException {
        if (str == null) {
            return null;
        }
        return compressFromBytes(str.getBytes(StandardCharsets.UTF_8));
    }

    public static String uncompress(byte[] bytes) throws IOException {
        byte[] bytes1 = uncompressToBytes(bytes);
        return new String(bytes1, StandardCharsets.UTF_8);
    }

    public static byte[] compressFromBytes(byte[] bytes) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
                gzip.write(bytes);
            }
            return out.toByteArray();
            //return out.toString(StandardCharsets.ISO_8859_1);
            // Some single byte encoding
        }
    }

    public static byte[] uncompressToBytes(byte[] bytes) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes))) {
            int b;
            while ((b = gis.read()) != -1) {
                baos.write((byte) b);
            }
        }
        return baos.toByteArray();
    }
}
