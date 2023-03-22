package com.jianyue.lightning.boot.starter.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author FLJ
 * @dateTime 2021/12/20 13:25
 * @description 流的util
 */
public class StreamUtil {

    // 列表forEach
    public static <T> Consumer<List<T>> forEach(Consumer<T> consumer) {
        return list -> list.forEach(consumer);
    }


    /**
     * 列表flatMap
     *
     * @param <T> T
     * @return Function<List < T>,Stream<T>>
     */
    public static <T> Function<List<T>, Stream<T>> listFlatMap() {
        return source -> Stream.of(CollectionUtil.toArray(source));
    }

    /**
     * 整体 map
     *
     * @param mapper t -> s 的映射器
     * @param <T>    T 类型
     * @param <S>    S 目标类型
     * @return 目标类型集合
     */
    public static <T, S> Function<List<T>, List<S>> listMap(Function<T, S> mapper) {
        return list -> list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 对list stream 进行 flatmap 收集
     *
     * @param <T> 当前集合元素类型
     * @return 集合列表
     */
    public static <T> Function<Stream<List<T>>, List<T>> listStreamFlatMap() {
        return stream -> stream.flatMap(listFlatMap()).collect(Collectors.toList());
    }

    /**
     * 整体 toMap
     *
     * @param <T> T 类型
     * @param <U> S 目标类型
     * @return 目标类型集合
     */
    public static <T, K, U> Function<List<T>, Map<K, U>> listToMap(Function<T, K> keyMapper, Function<T, U> valueMapper) {
        return list -> list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }


    /**
     * 列表flatMap
     *
     * @param <T> T
     * @return Function<List < T>,Stream<T>>
     */
    public static <T> Function<Set<T>, Stream<T>> setFlatMap() {
        return source -> Stream.of(CollectionUtil.toArray(source));
    }

    /**
     * 整体 map
     *
     * @param mapper t -> s 的映射器
     * @param <T>    T 类型
     * @param <S>    S 目标类型
     * @return 目标类型集合
     */
    public static <T, S> Function<Set<T>, Set<S>> setMap(Function<T, S> mapper) {
        return set -> set.stream().map(mapper).collect(Collectors.toSet());
    }

    /**
     * 对list stream 进行 flatmap 收集
     *
     * @param <T> 当前集合元素类型
     * @return 集合列表
     */
    public static <T> Function<Stream<Set<T>>, Set<T>> setStreamFlatMap() {
        return stream -> stream.flatMap(setFlatMap()).collect(Collectors.toSet());
    }

    /**
     * 整体 toMap
     *
     * @param <T> T 类型
     * @param <U> S 目标类型
     * @return 目标类型集合
     */
    public static <T, K, U> Function<Set<T>, Map<K, U>> setToMap(Function<T, K> keyMapper, Function<T, U> valueMapper) {
        return list -> list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }


    /**
     * 列表flatMap
     *
     * @param <T> T
     * @return Function<List < T>,Stream<T>>
     */
    public static <T> Function<Collection<T>, Stream<T>> collectionFlatMap() {
        return source -> Stream.of(CollectionUtil.toArray(source));
    }


    /**
     * 整体 map
     *
     * @param mapper t -> s 的映射器
     * @param <T>    T 类型
     * @param <S>    S 目标类型
     * @return 目标类型集合
     */
    public static <T, S> Function<Collection<T>, Collection<S>> collectionMap(Function<T, S> mapper) {
        return collection -> collection.stream().map(mapper).collect(Collectors.toSet());
    }

    /**
     * 对list stream 进行 flatmap 收集
     *
     * @param <T> 当前集合元素类型
     * @return 集合列表
     */
    public static <T> Function<Stream<Collection<T>>, List<T>> collectionStreamFlatMap() {
        return stream -> stream.flatMap(collectionFlatMap()).collect(Collectors.toList());
    }

    /**
     * 整体 toMap
     *
     * @param <T> T 类型
     * @param <U> S 目标类型
     * @return 目标类型集合
     */
    public static <T, K, U> Function<Collection<T>, Map<K, U>> collectionToMap(Function<T, K> keyMapper, Function<T, U> valueMapper) {
        return list -> list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

}
