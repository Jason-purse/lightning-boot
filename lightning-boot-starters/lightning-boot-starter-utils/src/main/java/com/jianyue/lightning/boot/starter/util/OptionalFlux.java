package com.jianyue.lightning.boot.starter.util;


import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author JASONJ
 * @dateTime: 2021-08-29 11:03:55
 * @description: optional flux
 */

public class OptionalFlux<S> {

    /**
     * hold value
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<S> value;

    /**
     * 无参消费者
     */
    @FunctionalInterface
    public interface NOArgConsumer {
        void accept();
    }


    /**
     * @param value optional value
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public OptionalFlux(Optional<S> value) {
        this.value = value;
    }

    public OptionalFlux(S value) {
        this(Optional.ofNullable(value));
    }

    /**
     * 静态方法 构造一个OptionalFlux
     *
     * @param value value optional
     * @param <S>   type
     * @return new OptionalFlux
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S> OptionalFlux<S> of(Optional<S> value) {
        return new OptionalFlux<>(value);
    }

    public static <S> OptionalFlux<S> of(S value) {
        return of(Optional.ofNullable(value));
    }

    public static <S> OptionalFlux<S> empty() {
        return OptionalFlux.of(Optional.empty());
    }

    public Optional<S> getValue() {
        return this.value;
    }

    // 消费null / real value
    public OptionalFlux<S> consumeOrNull(Consumer<S> consumer) {
        if (this.value.isPresent()) {
            consumer.accept(this.value.get());
        } else {
            consumer.accept(null);
        }
        return this;
    }

    public OptionalFlux<S> consume(Consumer<S> consumer) {
        this.value.ifPresent(consumer);
        return this;
    }

    public OptionalFlux<S> consume(NOArgConsumer consumer) {
        this.value.ifPresent(ele -> consumer.accept());
        return this;
    }

    public <T> OptionalFlux<T> cast(Class<T> targetClass) {
        return this.map(targetClass::cast).orElse(OptionalFlux.empty());
    }

    public  OptionalFlux<S> existsForThrowEx(RuntimeException ex) {
        if(this.isPresent()) {
            throw  ex;
        }
        return this;
    }

    public OptionalFlux<S> combine(OptionalFlux<S> other, BiFunction<S,S,S> handler) {
        if(other.isPresent() && this.isPresent()) {
            return OptionalFlux.of(handler.apply(getResult(),other.getResult()));
        }
        return this.isPresent() ? this : other;
    }


    public boolean isPresent() {
        return this.value.isPresent();
    }



    /**
     * if - else 逻辑
     *
     * @param supplier 不存在的映射
     * @return 返回OptionalFlux
     */
    public OptionalFlux<S> orElse(Supplier<S> supplier) {
        if (this.value.isEmpty()) {
            return OptionalFlux.of(supplier.get());
        }
        return this;
    }

    public OptionalFlux<S> orElse(S target) {
        if (this.value.isEmpty()) {
            return OptionalFlux.of(target);
        }
        return this;
    }

    public OptionalFlux<S> orElse(OptionalFlux<S> target){
        if (!isPresent()) {
            return target;
        }
        return this;
    }

    /**
     * 无参消费者
     *
     * @param consumer consumer
     */
    public void orElse(NOArgConsumer consumer) {
        if (this.value.isEmpty()) {
            consumer.accept();
        }
    }


    /**
     * map
     *
     * @param function map函数
     * @param <T>      type
     * @return result
     */
    public <T> OptionalFlux<T> map(Function<S, T> function) {
        Objects.requireNonNull(function);
        return new OptionalFlux<>(this.value.map(function));
    }

    /**
     * // switch map (三元表达式 推断)
     *
     * @param function if true exec
     * @param supplier if false exec
     * @param <T>      t type
     * @return new OptionalFlux
     */

    public <T> OptionalFlux<T> switchMap(Function<S, T> function, Supplier<T> supplier) {
        Objects.requireNonNull(function);
        Objects.requireNonNull(supplier);
        return this.map(function).orElse(supplier);
    }


    /**
     * 统一返回结果!
     *
     * @return null Or Object
     * <p>
     * if return null , may not call ifPresent  or OrElse or return result is null!
     */
    @SuppressWarnings("unchecked")
    public <T> T getResult() {
        return (T) this.value.orElse(null);
    }


    /**
     * @param clazz result class
     * @param <T>   T
     * @return result
     * @throws ClassCastException class can't converted to T
     */
    @Deprecated
    public <T> T getResult(Class<T> clazz) {
        return clazz.cast(getResult());
    }

    /**
     * @param eleType
     * @param <T>
     * @return
     * @throws ClassCastException class can't converted to T
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public <T> List<T> getResultForList(Class<T> eleType) {
        return ((List<T>) getResult());
    }

    @Nullable
    public Class<?> getTargetClass() {
        return this.map(Object::getClass).orElse(OptionalFlux.empty()).getResult();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OptionalFlux<?> that = (OptionalFlux<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this,value);
    }

    /**
     * TODO
     * <p>
     * <p>
     * 有一个需求,将任何类型转换为这个TypeRefer所指定的类型,前提是开发者知道这个已知类型,仅仅是将它转换为具体类型 ..
     * 例如 Object -> List<String>
     * Object -> Map<String,List<Map>
     * <p>
     * 如何做到这样的事情呢 ???
     *
     * @param <T>
     * @deprecated
     */
    public abstract static class TypeRefer<T> {

        public Type getGenericType() {
            Type type = this.getClass().getGenericSuperclass();
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return parameterizedType.getActualTypeArguments()[0];
        }
    }

    /**
     * 检查类型是否匹配 ...(不做隐式转换)
     * <p>
     * 例如 Set 属于 Collection ...(仅仅强类型比较)
     * 不考虑 协变
     *
     * @param source    source object
     * @param typeRefer type refer
     * @param <T>       generic type
     * @return T
     * @throws ClassCastException exception ..
     */
    @SuppressWarnings("unchecked")
    public static <T> T checkType(OptionalFlux<Object> source, TypeRefer<T> typeRefer) {

        if (!source.getValue().isPresent()) {
            // null
            return (T) null;
        }
        final Type genericType = typeRefer.getGenericType();
        // 直接复写 TypeRefer 的getGenericType方法 ...
        if (genericType instanceof Class) {
            source.getResult(((Class<T>) genericType));
        }

        // 否则参数化类型 ...
        else if (genericType instanceof ParameterizedType) {
            final ParameterizedType gType = (ParameterizedType) genericType;
            final Type[] actualTypeArguments = gType.getActualTypeArguments();
            // 尝试判断列表 ...
            final Type actualTypeArgument = actualTypeArguments[0];
            if (actualTypeArgument instanceof Class) {
                source.getResult(((Class<T>) actualTypeArgument));
            }

            // 否则有可能还是一个参数化类型
            if (actualTypeArgument instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) actualTypeArgument;

                Type rawType = parameterizedType.getRawType();
                // 这必须是一个 class
                if (isCollection(((Class<?>) rawType))) {
                    // 必然不为空,source
                    assert source.getTargetClass() != null;
                    Class<Collection<?>> collectionClass = (Class<Collection<?>>) rawType;
                    if (collectionClass.isAssignableFrom(source.getTargetClass())) {
                        Collection<?> cast = collectionClass.cast(source);
                        // 如果为空,则可以转过去 ...
                        if (cast.isEmpty()) {
                            return (T) cast;
                        }
                    }
                }

//				parameterizedType
            }


            // 这里比较复杂 ...
            final Class<?> aClass = source.getClass();
            // 如果是集合比较好办,一层一层比对 ...
            if (isCollection(aClass)) {
//				source.getResultForList()
            }

        } else {
            // 无法转换
            // 也就是必须传递 class / 参数化类型,不能是类型变量或者 通配符类型
            throw new ClassCastException("typeRefer must be one of class or parameterizedType !!!");
        }
        throw new ClassCastException("typeRefer must be one of class or parameterizedType !!!");
    }

    private static boolean isCollection(Class<?> clazz) {
        return Collection.class.isAssignableFrom(clazz);
    }


    private static boolean isMap(Class<?> clazz) {
        return Map.class.isAssignableFrom(clazz);
    }
}