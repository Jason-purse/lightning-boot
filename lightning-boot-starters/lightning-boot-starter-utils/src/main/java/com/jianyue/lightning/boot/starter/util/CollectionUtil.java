package com.jianyue.lightning.boot.starter.util;

import java.lang.reflect.Array;
import java.util.List;

public class CollectionUtil {

    /**
     * 空安全的列表-> 数组迭代器
     * @param list 列表
     * @param tClass 目标数组Class
     * @param <T> T 类型
     * @return 数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] iterator(List<T> list,Class<T> tClass) {
        List<T> ts = ElvisUtil.acquireNotNullList_Empty(list);
        T[] array = (T[])Array.newInstance(tClass, ts.size());
        for (int i = 0; i < ts.size(); i++) {
            array[i] = ts.get(i);
        }
        return array;
    }

    /**
     * 通过Object[] 强转为 T[]
     * @param list 列表
     * @param <T> T
     * @return T[]
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] iterator(List<T> list) {
        List<T> ts = ElvisUtil.acquireNotNullList_Empty(list);
        T[] array = (T[])Array.newInstance(Object.class, ts.size());
        for (int i = 0; i < ts.size(); i++) {
            array[i] = ts.get(i);
        }
        return array;
    }
}
