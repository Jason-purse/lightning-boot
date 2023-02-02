package com.jianyue.lightning.boot.starter.util;

import com.jianyue.lightning.exception.DefaultApplicationException;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Bean utilities.
 *
 * @author FLJ
 */
public class BeanUtils {

    private BeanUtils() {
    }

    /**
     * Transforms from the source object. (copy same properties only)
     *
     * @param source      source data
     * @param targetClass target class must not be null
     * @param <T>         target class type
     * @return instance with specified type copying from source data; or null if source data is null
     * @throws DefaultApplicationException if newing target instance failed or copying failed
     */
    @Nullable
    public static <T> T transformFrom(@Nullable Object source, @NonNull Class<T> targetClass) {
        Assert.notNull(targetClass, "Target class must not be null");

        if (source == null) {
            return null;
        }

        // Init the instance
        try {
            // New instance for the target class
            T targetInstance = targetClass.newInstance();
            // Copy properties
            org.springframework.beans.BeanUtils.copyProperties(source, targetInstance, getNullPropertyNames(source));
            // Return the target instance
            return targetInstance;
        } catch (Exception e) {
            throw DefaultApplicationException.of("Failed to new " + targetClass.getName() + " instance or copy properties", e);
        }
    }

    public static <T> SwitchUtil.Operation transformFrom(@Nullable Object source, @NonNull Class<T> targetClass, @Nullable Consumer<T> consumer) {
        return () -> {
            if (consumer != null) {
                consumer.accept(transformFrom(source, targetClass));
            }
        };
    }

    /**
     * 根据给定的目标类 将原始数据抓话.
     *
     * @param targetClass 目标类
     * @param <T>         原始类型
     * @param <R>         目标类型
     * @return 返回一个转义表达式
     */
    public static <T, R> Function<T, R> transformFrom(Class<R> targetClass) {
        return (T source) -> transformFrom(source, targetClass);
    }

    public static <T, R> Function<T, R> transformTo(Class<R> targetClass, @Nullable Consumer<R> consumer) {
        return (T source) -> {
            R r = transformFrom(source, targetClass);
            if (consumer != null) {
                consumer.accept(r);
            }
            return r;
        };
    }

    public static <T, R> Function<T, R> transformTo(Class<R> targetClass, @Nullable BiConsumer<T, R> consumer) {
        return (T source) -> {
            R r = transformFrom(source, targetClass);
            if (consumer != null) {
                consumer.accept(source, r);
            }
            return r;
        };
    }

    public static <T, R> Consumer<T> transformFrom(Class<R> targetClass, @Nullable Consumer<R> consumer) {
        return (T source) -> {
            R r = transformFrom(source, targetClass);
            if (consumer != null) {
                consumer.accept(r);
            }
        };
    }

    /**
     * Transforms from source data collection in batch.
     *
     * @param sources     source data collection
     * @param targetClass target class must not be null
     * @param <T>         target class type
     * @return target collection transforming from source data collection.
     * @throws DefaultApplicationException if newing target instance failed or copying failed
     */
    @NonNull
    public static <T> List<T> transformFromInBatch(Collection<?> sources, @NonNull Class<T> targetClass) {
        if (CollectionUtils.isEmpty(sources)) {
            return Collections.emptyList();
        }
        // Transform in batch
        return sources.stream()
                .map(source -> transformFrom(source, targetClass))
                .collect(Collectors.toList());
    }

    /**
     * Update properties (non null).
     *
     * @param source source data must not be null
     * @param target target data must not be null
     * @throws DefaultApplicationException if copying failed
     */
    public static void updateProperties(@NonNull Object source, @NonNull Object target) {
        Assert.notNull(source, "source object must not be null");
        Assert.notNull(target, "target object must not be null");

        // Set non null properties from source properties to target properties
        try {
            org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        } catch (Exception e) {
            throw new DefaultApplicationException("Failed to copy properties", e);
        }
    }

    /**
     * 根据给定的目标 进行数据消费..
     *
     * @param <T> 原始类型
     * @param <R> 目标类型
     * @return 返回一个转义表达式
     */
    public static <T, R> Consumer<T> updateProperties(R target) {
        return (T source) -> updateProperties(source, target);
    }

    /**
     * 反转更新
     *
     * @param source 被消费的数据
     * @param <T>    原始消息
     * @param <R>    目标消息
     * @return 表达式
     */
    public static <T, R> Consumer<T> updatePropertiesReverse(R source) {
        return (T target) -> updateProperties(source, target);
    }

    /**
     * 更新数据且后续消费..(目标数据)
     *
     * @param target   被更新的目标
     * @param consumer 消费器
     * @param <T>      目标类型
     * @param <R>      原始类型
     * @return 表达式
     */
    public static <T, R> Consumer<T> updateProperties(R target, @Nullable Consumer<R> consumer) {
        return (T source) -> {
            updateProperties(source, target);
            if (consumer != null) {
                consumer.accept(target);
            }
        };
    }

    /**
     * 反转消费..(消费target)
     *
     * @param source   源数据
     * @param consumer 更新数据消费者
     * @param <T>      更新数据类型
     * @param <R>      源数据类型
     * @return 表达式..
     */
    public static <T, R> Consumer<T> updatePropertiesReverse(R source, @Nullable Consumer<T> consumer) {
        return (T target) -> {
            updateProperties(source, target);
            if (consumer != null) {
                consumer.accept(target);
            }
        };
    }

    /**
     * Gets null names array of property.
     *
     * @param source object data must not be null
     * @return null name array of property
     */
    @NonNull
    private static String[] getNullPropertyNames(@NonNull Object source) {
        return getNullPropertyNameSet(source).toArray(new String[0]);
    }

    /**
     * Gets null names set of property.
     *
     * @param source object data must not be null
     * @return null name set of property
     */
    @NonNull
    private static Set<String> getNullPropertyNameSet(@NonNull Object source) {

        Assert.notNull(source, "source object must not be null");
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();

        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String propertyName = propertyDescriptor.getName();
            Object propertyValue = beanWrapper.getPropertyValue(propertyName);

            // if property value is equal to null, add it to empty name set
            if (propertyValue == null) {
                emptyNames.add(propertyName);
            }
        }

        return emptyNames;
    }
}