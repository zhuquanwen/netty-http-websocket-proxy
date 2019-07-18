package com.iscas.common.tools.pagination;

import java.util.List;

/**
 * 内存分页工具类
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/4/12 14:17
 * @since jdk1.8
 */
public class MemoryPageUtils {
    private MemoryPageUtils() {}

    /**
     *
     * 内存内部对集合进行分页
     * */
    public static <T> List<T> getPageList(List<T> list, int pageNumber, int pageSize) {
        if (list == null) {
            return null;
        }
        assert pageNumber > 0;
        assert pageSize > 0;
        int start = pageSize * (pageNumber - 1);
        int end = pageSize * pageNumber;
        if (list.size() < end) {
            end = list.size();
        }
        return list.subList(start, end);
    }
}
