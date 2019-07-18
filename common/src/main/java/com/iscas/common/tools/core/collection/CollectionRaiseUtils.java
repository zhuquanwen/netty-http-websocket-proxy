package com.iscas.common.tools.core.collection;

import java.util.Collection;
import java.util.Iterator;

/**
 * //TODO
 *
 * @author zhuquanwen
 * @vesion 1.0
 * @date 2019/4/19 14:45
 * @since jdk1.8
 */
public class CollectionRaiseUtils {
    private CollectionRaiseUtils() {}

    /**
     * 判断集合是不是为空，如果集合里全都是null也判断
     * */
    public static boolean isEmpty(Collection collection) {
        boolean flag = collection == null || collection.size() <= 0;
        if (!flag) {
            Iterator iterator = collection.iterator();
            boolean notnullFlag = false;
            while (iterator.hasNext()) {
                if (iterator.next() != null) {
                    notnullFlag = true;
                    break;
                }
            }
            flag = !notnullFlag;
        }
        return flag;
    }

    /**
     * 判断集合是不是不为空，如果集合里全都是null也判断
     * */
    public static boolean isNotEmpty(Collection collection) {
        return !isEmpty(collection);
    }
}
