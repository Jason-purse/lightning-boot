package com.jianyue.lightning.boot.starter.util;

import java.util.List;
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

    /**
     * 列表flatMap
     * @param <T> T
     * @return Function<List<T>,Stream<T>>
     */
    public static <T> Function<List<T>,Stream<T>> listFlatMap() {
        return source -> Stream.of(CollectionUtil.iterator(source));
    }
    // 列表forEach
    public static <T> Consumer<List<T>> forEach(Consumer<T> consumer) {
        return list -> list.forEach(consumer);
    }

    /**
     * 整体 map
     * @param mapper t -> s 的映射器
     * @param <T> T 类型
     * @param <S> S 目标类型
     * @return 目标类型集合
     */
    public static <T,S> Function<List<T>,List<S>> listMap(Function<T,S> mapper) {
        return list -> Stream.of(CollectionUtil.iterator(list)).map(mapper).collect(Collectors.toList());
    }

    /**
     * 对list stream 进行 flatmap 收集
     * @param <T> 当前集合元素类型
     * @return 集合列表
     */
    public static <T> Function<Stream<List<T>>,List<T>> listStreamFlatMap() {
        return stream -> stream.flatMap(listFlatMap()).collect(Collectors.toList());
    }
}
