package com.jianyue.lightning.boot.starter.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author FLJ
 * @dateTime 2021/12/20 13:01
 * @description ?: 形式的操作符
 */
public class ElvisUtil {
    public static <T> Collection<T> acquireNotNullCollection(Collection<T> collection, Collection<T> defaultCollection) {
        if (CollectionUtils.isNotEmpty(collection)) {
            return collection;
        }
        return defaultCollection;
    }

    public static <T> Collection<T> acquireNotNullCollection_EmptyList(Collection<T> collection) {
        return acquireNotNullCollection(collection, Collections.emptyList());
    }

    public static <K, V> Map<K, V> acquireNotNullMap(Map<K, V> map, Map<K, V> defaultMap) {
        if (MapUtils.isNotEmpty(map)) {
            return map;
        }
        return defaultMap;
    }

    public static <K, V> Map<K, V> acquireNotNullMap_Empty(Map<K, V> map) {
        return acquireNotNullMap(map, Collections.emptyMap());
    }

    public static <T> T[] acquireNotNullArray(T[] array, T[] defaultArray) {
        if (ObjectUtils.isEmpty(array)) {
            return defaultArray;
        }
        return array;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] acquireNotNullArray_Empty(T[] array, Class<T> tClass) {
        T[] sequence = (T[]) Array.newInstance(tClass, 0);
        return acquireNotNullArray(array, sequence);
    }

    public static <T> List<T> acquireNotNullList(List<T> list, List<T> defaultList) {
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return defaultList;
    }

    public static <T> List<T> acquireNotNullList_Empty(List<T> list) {
        return acquireNotNullList(list, Collections.emptyList());
    }

    public static <T> Collection<T> acquireNotNullList(Collection<T> list, Collection<T> defaultList) {
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return defaultList;
    }

    public static <T> Collection<T> acquireNotNullList_Empty(Collection<T> list) {
        return acquireNotNullList(list, Collections.emptyList());
    }

    public static <T> void isNotEmptyConsumer(T target, Consumer<T> consumer) {
        if (!ObjectUtils.isEmpty(target)) {
            Objects.requireNonNull(consumer, "consumer must not be null!")
                    .accept(target);
        }
    }

    public static <T, S> S isNotEmptyFunction(T target, Function<T, S> function) {
        if (!ObjectUtils.isEmpty(target)) {
            return Objects.requireNonNull(function, "function must not be null!")
                    .apply(target);
        }
        return null;
    }

    public static <T, S> S isNotEmptySupplier(T target, Supplier<S> function) {
        if (ObjectUtils.isEmpty(target)) {
            return Objects.requireNonNull(function, "function must not be null!")
                    .get();
        }
        return null;
    }

    public static <T> void isEmptyConsumer(T target, NOArgConsumer consumer) {
        if (ObjectUtils.isEmpty(target)) {
            Objects.requireNonNull(consumer, "consumer must not be null!")
                    .accept();
        }
    }


    public static <T, S> S isEmptySupplier(T target, Supplier<S> function) {
        if (ObjectUtils.isEmpty(target)) {
            return Objects.requireNonNull(function, "function must not be null!")
                    .get();
        }
        return null;
    }


    public static <S> S getOrDefault(S source, Supplier<S> targetSupplier) {
        return Objects.requireNonNullElseGet(source, targetSupplier);
    }

    public static <S> S getOrDefault(S source, S other) {
        return Objects.requireNonNullElse(source, other);
    }


    public static Integer intElvis(Integer value, Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Integer intOrZero(Integer value) {
        return intElvis(value, 0);
    }

    public static Long longElvis(Long value,Long defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Long longOrZero(Long value) {
        return longElvis(value,0L);
    }

    public static Double doubleElvis(Double value, Double defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Double doubleOrZero(Double value) {
        return doubleElvis(value, 0D);
    }

    public static Double doubleOrZero(String value) {
        try {
            Double aDouble = Double.valueOf(value);
            return doubleOrZero(aDouble);
        } catch (Exception e) {
            // pass
        }
        return 0D;
    }

    public static String stringElvis(String value, String defaultValue) {
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    public static String stringElvisOrNull(String value) {
        return stringElvis(value, null);
    }

    public static <T> Collection<T> collectionElvis(Collection<T> first, Collection<T> second) {
        return ElvisUtil.acquireNotNullList(first, second);
    }

    public static <T> Collection<T> collectionElvisOrNull(Collection<T> first) {
        return ElvisUtil.acquireNotNullList(first, null);
    }

    public static <T> List<T> listElvis(List<T> first, List<T> second) {
        return ElvisUtil.acquireNotNullList(first, second);
    }

    public static <T> List<T> listElvisOrNull(List<T> first) {
        return ElvisUtil.acquireNotNullList(first, null);
    }
}
