package com.iscas.cs.server.proxy.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/7/3 9:14
 * @since jdk1.8
 */
@Data
@Accessors(chain = true)
public class CacheTactics {
//    /**缓存策略*/
//    private String dataCacheTactics = "-1";
//
//    /**缓存回放策略*/
//    private String playbackTactics = "-1";
//
//    /**
//     * 缓存获取数据策略
//     * */
//    private String getDataCacheTactics = "-1";

    /**
     * JS压缩策略
     * */
    private String jsCompressTactics = "-1";

    /**
     * CSS压缩策略
     * */
    private String cssCompressTactics = "-1";

    /**
     * HTML压缩策略
     * */
    private String htmlCompressTactics = "-1";

    /**
     * 下载文件存储路径
     * */
    private String downFilePath;

    /**
     * 上传文件存储路径
     * */
    private String uploadFilePath;

    /**
     * 回放数据的临界值大小（M）
     * */
    private int playbackThresholdSize = 3;

    private int playbackThresholdByteSize = 3 * 1024 * 1024;

    /**
     * 回放数据磁盘存储路径
     * */
    private String playbackPath;

    /**
     * dll文件路径
     * */
    private String dllPath;
}
