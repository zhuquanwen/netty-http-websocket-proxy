package com.iscas.cs.server.proxy.netty.http;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.StreamProgress;
import com.iscas.common.tools.core.date.DateSafeUtils;
import com.iscas.common.tools.core.string.StringRaiseUtils;
import com.iscas.cs.server.bean.Component;
import com.iscas.cs.server.proxy.base.Constant;
import com.iscas.cs.server.proxy.model.CacheTactics;
import com.iscas.cs.server.proxy.model.ProxySetting;
import com.iscas.cs.server.proxy.util.BytesUtils;
import com.iscas.cs.server.proxy.util.ConfigUtils;
import com.iscas.cs.server.proxy.util.HttpUtils;
import com.iscas.cs.server.proxy.util.MapRaiseUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/9 14:01
 * @since jdk1.8
 */
@Slf4j
@Component
public class DownloadFileHandler {
    private CacheTactics cacheTactics;
    public DownloadFileHandler() {
        cacheTactics = ConfigUtils.getCacheTacticsProps();
    }
    public static DownloadFileHandler getInstance() {
        return DownloadFileHandlerHolder.instance;
    }

    /**
     * 判断是否为下载文件的请求,先判断请求header再判断URL参数
     * */
    public boolean checkFileDown(FullHttpRequest request) {
        String downFile = HttpUtils.getHeader(request, Constant.CS_DOWNFILE);
        if (Objects.equals("1", downFile)) {
            //请求为文件下载
            return true;
        } else {
            downFile = HttpUtils.getRequestParam(request, Constant.CS_DOWNFILE);
            if (Objects.equals("1", downFile)) {
                //请求为文件下载
                return true;
            }
        }
        return false;
    }




    /**
     * 下载文件缓存处理
     * */
    public byte[] cacheFile(byte[] dataBytes, FullHttpRequest request, FullHttpResponse response) {
        //获取文件名
        String fileName = null;
        String str = HttpUtils.getHeader(response, "Content-disposition");
        if (str != null && str.contains("filename=")) {
            fileName = StringUtils.substringAfterLast(str, "filename=");
        }
        ProxySetting servletSetting = HttpUtils.getRouteSetting(request);
        if (servletSetting != null) {
            String proxyUrl = servletSetting.getProxyUrl();
            proxyUrl = StringRaiseUtils.deleteAllString(proxyUrl, "*");
            proxyUrl = StringRaiseUtils.deleteAllString(proxyUrl, "/");
            File downloadFilePFile = getDownloadFileParentPath(proxyUrl);

            //使用UUID作为文件名
            String storeFileName = UUID.randomUUID().toString();
            File storeFile = new File(downloadFilePFile, storeFileName);
            try {
                @Cleanup OutputStream os = new FileOutputStream(storeFile);
                @Cleanup ByteArrayInputStream bais = new ByteArrayInputStream(dataBytes);
                IoUtil.copyByNIO(bais, os, 8192, new StreamProgress() {
                    @Override
                    public void start() {
                    }
                    @Override
                    public void progress(long progressSize) {
                    }
                    @Override
                    public void finish() {
                    }
                });
                String value = fileName == null ? storeFile.getAbsolutePath() : storeFile.getAbsolutePath().concat(";").concat(fileName);
                return BytesUtils.strToBytes(value);
            } catch (Exception e) {
                log.warn("缓存文件时出错", e);
                throw new RuntimeException("Error while caching files", e);
            }

        }
        return null;
    }

    /**
     * 获取下载文件缓存处理,如果直接返回下载数据了返回true
     * */
    public boolean getCacheFile(Map<byte[], byte[]> bytesMap, FullHttpRequest request, ChannelHandlerContext ctx) {
        if (bytesMap != null) {
            try {
                byte[] filePathBytes = MapRaiseUtils.getWithBytesKey(bytesMap, BytesUtils.strToBytes(Constant.DOWNFILE_CACHE_KEY));
                if (filePathBytes != null) {
                    String filePaths = BytesUtils.bytesToStr(filePathBytes);
                    if (StringUtils.isNotEmpty(filePaths)) {
                        String filePath = null;
                        String fileName = null;
                        if (filePaths.contains(";")) {
                            filePath = StringUtils.substringBefore(filePaths, ";");
                            fileName = StringUtils.substringAfterLast(filePaths, ";");
                        } else {
                            filePath = filePaths;
                        }
                        File file = new File(filePath);
                        if (filePath == null || !file.exists()) {
                            log.warn("未在磁盘找到缓存文件");
                            throw new RuntimeException("The cache file was not found on the disk");
                        }
                        //直接把文件下载回去
                        if (fileName == null) {
                            fileName = file.getName();
                        }

                        //TODO 这里是不是应该有更好的读文件的方法
                        @Cleanup InputStream is = new FileInputStream(file);
                        @Cleanup ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IoUtil.copy(is, baos);
                        ByteBuf byteBuf = Unpooled.wrappedBuffer(baos.toByteArray());
                        FullHttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK,
                                byteBuf);
                        HttpUtils.setContentType(response, "application/octet-stream;charset=utf-8");
                        HttpUtils.setHeader(response, "Content-disposition", "attachment; filename="
                                +fileName);
                        HttpUtils.sendResponse(request ,response, ctx);
                        return true;
                    }
                }
            } catch (Exception e) {
                log.warn("URL：{}获取文件下载的缓存数据出错", request.uri());
                throw new RuntimeException("Error getting cached data for file download", e);
            }
        }
        return false;
    }

    private File getDownloadFileParentPath(String key) {
        String downFilePath = cacheTactics.getDownFilePath();
        if (downFilePath == null) {
            throw new RuntimeException("In the configuration file: cache-tactics.properties configuration 'downfile-path' cannot be empty");
        }
        File file = new File(downFilePath);
        //双重校验锁并发时提升效率
        if (!file.exists()) {
            synchronized (this) {
                if (!file.exists()) {
                    file.mkdirs();
                }
            }
        }
        File keyFile = new File(file, key);
        //双重校验锁并发时提升效率
        if (!keyFile.exists()) {
            synchronized (this) {
                if (!keyFile.exists()) {
                    keyFile.mkdirs();
                }
            }
        }
        File resultFile = new File(keyFile, DateSafeUtils.format(new Date(), "yyyy-MM-dd"));
        //双重校验锁并发时提升效率
        if (!resultFile.exists()) {
            synchronized (this) {
                if (!resultFile.exists()) {
                    resultFile.mkdirs();
                }
            }
        }
        return resultFile;
    }

    static class DownloadFileHandlerHolder {
        public static DownloadFileHandler instance = new DownloadFileHandler();
    }


}
