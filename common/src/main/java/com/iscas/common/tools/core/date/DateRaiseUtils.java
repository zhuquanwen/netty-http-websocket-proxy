package com.iscas.common.tools.core.date;

import java.util.Date;

/**
 * <p>date操作增强类</p>
 * @author zhuquanwen
 * @version 1.0
 * @since jdk1.8
 */

public class DateRaiseUtils {
    private DateRaiseUtils(){}

    /**
     * 获取当前日期里的年份
     * @version 1.0
     * @since jdk1.8
     * @param date 日期
     * @return int 年份
     * @see DateSafeUtils
     */
    public static int getYear(Date date){
        assert date != null;
        String x = DateSafeUtils.format(date, "yyyy");
        return Integer.parseInt(x);
    }

    /**
     * 获取当前日期里的月份
     * @version 1.0
     * @since jdk1.8
     * @param date 日期
     * @return int 月份
     * @see DateSafeUtils
     */
    public static int getMonth(Date date){
        assert date != null;
        String month = DateSafeUtils.format(date, "MM");
        return Integer.parseInt(month);
    }

    /**
     * 获取当前日期里的天
     * @version 1.0
     * @since jdk1.8
     * @param date 日期
     * @return int 天
     * @see DateSafeUtils
     */
    public static int getDay(Date date){
        assert date != null;
        String x = DateSafeUtils.format(date, "dd");
        return Integer.parseInt(x);
    }

    /**
     * 获取当前日期里的小时
     * @version 1.0
     * @since jdk1.8
     * @param date 日期
     * @return int 小时
     * @see DateSafeUtils
     */
    public static int getHour(Date date){
        assert date != null;
        String x = DateSafeUtils.format(date, "HH");
        return Integer.parseInt(x);
    }
    /**
     * 获取当前日期里的分钟
     * @version 1.0
     * @since jdk1.8
     * @param date 日期
     * @return int 分钟
     * @see DateSafeUtils
     */
    public static int getMinute(Date date){
        assert date != null;
        String x = DateSafeUtils.format(date, "HH");
        return Integer.parseInt(x);
    }

    /**
     * 获取当前日期里的秒数
     * @version 1.0
     * @since jdk1.8
     * @param date 日期
     * @return int 秒数
     * @see DateSafeUtils
     */
    public static int getSecond(Date date){
        assert date != null;
        String x = DateSafeUtils.format(date, "ss");
        return Integer.parseInt(x);
    }

    /**
     * 判断当前月份是否是季度末
     * @version 1.0
     * @since jdk1.8
     * @param date 时间
     * @return boolean
     * @see #getMonth(Date)
     */
    @SuppressWarnings("AlibabaUndefineMagicConstant")
    public static boolean isSeason(Date date){
        assert date != null;
        boolean sign = false;
        int month = getMonth(date);
        if (month==3) {
            sign = true;
        }
        if (month==6) {
            sign = true;
        }
        if (month==9) {
            sign = true;
        }
        if (month==12) {
            sign = true;
        }
        return sign;
    }
    /**
     * 计算从现在开始偏移毫秒数后的时间,支持负数
     * @version 1.0
     * @since jdk1.8
     * @param offset 偏移的时候毫秒数
     * @return java.util.Date
     * @see #afterOffsetDate(Date, long)
     */
    public static Date afterOffsetDate(long offset){
        return afterOffsetDate(new Date(), offset);
    }

    /**
     * 计算从某个时间偏移毫秒数后的时间,支持负数
     * @version 1.0
     * @since jdk1.8
     * @param offset 偏移的时候毫秒数
     * @param date 日期时间
     * @return java.util.Date
     */
    public static Date afterOffsetDate(Date date, long offset){
        assert date != null;
        long time = date.getTime() * 1L;
        time = time + offset;
        return new Date(time);
    }
}
