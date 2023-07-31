package com.jianyue.lightning.boot.starter.util;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author FLJ
 * @dateTime 2021/12/20 13:01
 * @description ?: 形式的操作符(elvis 操作符)
 */
public class ElvisUtil {


    @NotNull
    public static <K, V> Map<K, V> getMap(@Nullable Map<K, V> map, @NotNull Map<K, V> defaultMap) {
        if (MapUtils.isNotEmpty(map)) {
            return map;
        }
        return defaultMap;
    }

    @NotNull
    public static <K, V> Map<K, V> getMapOrEmpty(@Nullable Map<K, V> map) {
        return getMap(map, Collections.emptyMap());
    }

    @NotNull
    public static <T> T[] getArray(@Nullable T[] array, @NotNull T[] defaultArray) {
        if (isEmpty(array)) {
            return defaultArray;
        }

        return array;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T[] getArrayOrEmpty(@Nullable T[] array, Class<T> tClass) {

        if (isEmpty(array)) {
            return (T[]) Array.newInstance(tClass, 0);
        }
        return array;
    }

    @NotNull
    public static <T> List<T> getList(@Nullable List<T> list, @NotNull List<T> defaultList) {
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return defaultList;
    }

    @NotNull
    public static <T> List<T> getListOrEmpty(@Nullable List<T> list) {
        return getList(list, Collections.emptyList());
    }

    @NotNull
    public static <T> Collection<T> getCollection(@Nullable Collection<T> list, @NotNull Collection<T> defaultList) {
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        return defaultList;
    }

    @NotNull
    public static <T> Collection<T> getCollectionOrEmpty(@Nullable Collection<T> list) {
        return getCollection(list, Collections.emptyList());
    }


    public static <T> void isNotEmptyConsumer(@Nullable T target, @NotNull Consumer<@NotNull T> consumer) {
        if (!isEmpty(target)) {
            consumer.accept(target);
        }
    }

    @Nullable
    public static <T, S> S isNotEmptyFunction(@Nullable T target, @NotNull Function<@NotNull T, @NotNull S> function) {
        if (!isEmpty(target)) {
            return function.apply(target);
        }
        return null;
    }

    @Nullable
    public static <T, S> S isNotEmptySupplier(@Nullable T target, @NotNull Supplier<S> function) {
        if (isEmpty(target)) {
            return function.get();
        }
        return null;
    }

    public static <T> void isEmptyConsumer(@Nullable T target, @NotNull OptionalUtil.NOArgConsumer consumer) {
        if (isEmpty(target)) {
            consumer.accept();
        }
    }


    @Nullable
    public static <T, S> S isEmptySupplier(@Nullable T target, @NotNull Supplier<@NotNull S> function) {
        if (isEmpty(target)) {
            return function.get();
        }
        return null;
    }


    @NotNull
    public static <S> S getOrDefault(@Nullable S source, @NotNull Supplier<@NotNull S> targetSupplier) {
        S value = isEmptySupplier(source, targetSupplier);
        assert value != null;
        return value;
    }

    @NotNull
    public static <S> S getOrDefault(@Nullable S source, @NotNull S other) {
        S value = isEmptySupplier(source, () -> other);
        assert value != null;
        return value;
    }


    @NotNull
    public static Integer intElvis(@Nullable Integer value, @NotNull Integer defaultValue) {
        return value == null ? defaultValue : value;
    }

    @NotNull
    public static Integer intOrZero(@Nullable Integer value) {
        return intElvis(value, 0);
    }

    @NotNull
    public static Double doubleElvis(@Nullable Double value, @NotNull Double defaultValue) {
        return value == null ? defaultValue : value;
    }

    @NotNull
    public static Double doubleOrZero(@Nullable Double value) {
        return doubleElvis(value, 0D);
    }

    @NotNull
    public static Double doubleOrZero(@Nullable String value) {
        try {
            Double aDouble = Double.valueOf(value);
            return doubleOrZero(aDouble);
        } catch (Exception e) {
            // pass
        }
        return 0D;
    }

    @NotNull
    public static String stringElvis(@Nullable String value, @NotNull String defaultValue) {
        if (isEmpty(defaultValue)) {
            throw new IllegalArgumentException("default value must not be blank");
        }
        return StringUtils.isNotBlank(value) ? value : defaultValue;
    }

    @Nullable
    public static String stringElvisOrNull(@Nullable String value) {
        return isEmpty(value) ? null : value;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> @Nullable T getOrNull(@NotNull Optional<T> value) {
        return value.orElse(null);
    }

    @NotNull
    public static <T> Collection<T> collectionElvis(@Nullable Collection<T> first, @NotNull Collection<T> second) {
        return ElvisUtil.getCollection(first, second);
    }

    @Nullable
    public static <T> Collection<T> collectionElvisOrNull(@Nullable Collection<T> first) {
        return isEmpty(first) ? null : first;
    }

    @NotNull
    public static <T> List<T> listElvis(@Nullable List<T> first, @NotNull List<T> second) {
        return ElvisUtil.getList(first, second);
    }

    @Nullable
    public static <T> List<T> listElvisOrNull(@NotNull List<T> first) {
        return isEmpty(first) ? null : first;
    }


    public static boolean isEmpty(@Nullable final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        return false;
    }

}