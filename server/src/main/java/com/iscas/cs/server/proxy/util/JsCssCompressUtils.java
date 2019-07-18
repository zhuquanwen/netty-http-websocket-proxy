package com.iscas.cs.server.proxy.util;

import com.yahoo.platform.yui.compressor.CssCompressor;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import lombok.Cleanup;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

import java.io.*;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/8 15:35
 * @since jdk1.8
 */
public class JsCssCompressUtils {
    private JsCssCompressUtils() {}


    public static byte[] csscompress(byte[] content) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        @Cleanup Reader in = new InputStreamReader(bais);
        CssCompressor csscompressor = new CssCompressor(in);
        @Cleanup OutputStream os = new ByteArrayOutputStream();
        @Cleanup Writer writer = new PrintWriter(os);
        csscompressor.compress(writer, -1);
        writer.flush();
        byte[] bytes = ((ByteArrayOutputStream) os).toByteArray();
        return bytes;
    }

    public static String csscompress(String content) throws IOException {
        @Cleanup Reader in = new InputStreamReader(IOUtils.toInputStream(content, "utf-8"));
        CssCompressor csscompressor = new CssCompressor(in);
        @Cleanup OutputStream os = new ByteArrayOutputStream();
        @Cleanup Writer writer = new PrintWriter(os);
        csscompressor.compress(writer, -1);
        writer.flush();
        byte[] bytes = ((ByteArrayOutputStream) os).toByteArray();
        return new String(bytes, "utf-8");
    }

    public static byte[] jscompress(byte[] content, String type) throws IOException {
        if (content == null || content.length == 0) {
            return content;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        @Cleanup Reader in = new InputStreamReader(bais);
        JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {
            @Override
            public void warning(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    System.err.println("/n[WARNING] " + message);
                } else {
                    System.err.println("/n[WARNING] " + line + ':' + lineOffset + ':' + message);
                }
            }
            @Override
            public void error(String message, String sourceName,
                              int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    System.err.println("/n[ERROR] " + message);
                } else {
                    System.err.println("/n[ERROR] " + line + ':' + lineOffset + ':' + message);
                }
            }
            @Override
            public EvaluatorException runtimeError(String message, String sourceName,
                                                   int line, String lineSource, int lineOffset) {
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }
        });
        @Cleanup OutputStream os = new ByteArrayOutputStream();
        @Cleanup Writer writer = new PrintWriter(os);
        if(type!=null && type.equals("yui")){
            compressor.compress(writer, -1, true, false, false, false);
        }else if(type!=null && type.equals("pack")){//普通压缩
            compressor.compress(writer, 0, true, false, false, false);
        }
        writer.flush();
        byte[] bytes = ((ByteArrayOutputStream) os).toByteArray();
        return bytes;
    }

    public static String jscompress(String content, String type) throws IOException {
        @Cleanup Reader in = new InputStreamReader(IOUtils.toInputStream(content, "utf-8"));
        JavaScriptCompressor compressor = new JavaScriptCompressor(in, new ErrorReporter() {
            @Override
            public void warning(String message, String sourceName,
                                int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    System.err.println("/n[WARNING] " + message);
                } else {
                    System.err.println("/n[WARNING] " + line + ':' + lineOffset + ':' + message);
                }
            }
            @Override
            public void error(String message, String sourceName,
                              int line, String lineSource, int lineOffset) {
                if (line < 0) {
                    System.err.println("/n[ERROR] " + message);
                } else {
                    System.err.println("/n[ERROR] " + line + ':' + lineOffset + ':' + message);
                }
            }
            @Override
            public EvaluatorException runtimeError(String message, String sourceName,
                                                   int line, String lineSource, int lineOffset) {
                error(message, sourceName, line, lineSource, lineOffset);
                return new EvaluatorException(message);
            }
        });
        @Cleanup OutputStream os = new ByteArrayOutputStream();
        @Cleanup Writer writer = new PrintWriter(os);
        if(type!=null && type.equals("yui")){
            compressor.compress(writer, -1, true, false, false, false);
        }else if(type!=null && type.equals("pack")){//普通压缩
            compressor.compress(writer, 0, true, false, false, false);
        }
        writer.flush();
        byte[] bytes = ((ByteArrayOutputStream) os).toByteArray();
        return new String(bytes, "utf-8");
    }

//    public static void main(String[] args) {
//        String str = "function checkIfExist() {\n" +
//                "\t\t\t//啦啦啦\n" +
//                "\t\t\t\n" +
//                "\t\t\tvar username = '张三';\n" +
//                "\t\t\t\n" +
//                "\t\t\treturn \"wegw\";\n" +
//                "\t\t}";
//        try {
//            String packJs = jscompress(str, "yui");
//            System.out.println(packJs);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
