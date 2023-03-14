package com.jianyue.lightning.boot.starter.util;

import org.jetbrains.annotations.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author FLJ
 * @date 2023/3/14
 * @time 12:53
 * @Description 条件util
 */
public class PredicateUtil {

    public static <T> Predicate<T> nullSafeEquals(@Nullable Object other) {
        return ele -> Objects.equals(ele, other);
    }


    public static Predicate<Boolean> nullSafeIsTrue() {
        return Boolean.TRUE::equals;
    }


    public static Predicate<Boolean> nullSafeIsFalse() {
        return ele -> !nullSafeIsTrue().test(ele);
    }

    /**
     * greater than
     */
    public static Predicate<Integer> nullSafeIntegerGt(@Nullable Integer other) {
        return ele -> ele != null && other != null && ele > other;
    }

    public static Predicate<Integer> nullSafeIntegerGte(@Nullable Integer other) {
        return ele -> ele != null && other != null && ele >= other;
    }


    public static Predicate<Integer> nullSafeIntegerLte(@Nullable Integer other) {
        return ele -> ele != null && other != null && ele <= other;
    }

    /**
     * less than
     */
    public static Predicate<Integer> nullSafeIntegerLt(@Nullable Integer other) {
        return ele -> ele != null && other != null && ele < other;
    }

    public static Predicate<Double> nullSafeDoubleGt(@Nullable Double other) {
        return ele -> ele != null && other != null && ele > other;
    }

    public static Predicate<Double> nullSafeDoubleLt(@Nullable Double other) {
        return ele -> ele != null && other != null && ele < other;
    }

    public static Predicate<Double> nullSafeDoubleGte(@Nullable Double other) {
        return ele -> ele != null && other != null && ele >= other;
    }


    public static Predicate<Double> nullSafeDoubleLte(@Nullable Double other) {
        return ele -> ele != null && other != null && ele <= other;
    }


    public static Predicate<String> isNotBlank() {
        return StringUtils::hasText;
    }

    public static Predicate<String> isBlank() {
        return ele ->  !isNotBlank().test(ele);
    }

    public static Predicate<String> isEmptyString() {
        return org.apache.commons.lang3.StringUtils::isEmpty;
    }

    public static Predicate<String> isNotEmptyString() {
        return ele -> !isEmptyString().test(ele);
    }

    public static <T>  Predicate<? extends Collection<T>> isEmpty() {
        return CollectionUtils::isEmpty;
    }

    public static <T> Predicate<Collection<T>> isNotEmpty() {
        return org.apache.commons.collections.CollectionUtils::isNotEmpty;
    }

}
