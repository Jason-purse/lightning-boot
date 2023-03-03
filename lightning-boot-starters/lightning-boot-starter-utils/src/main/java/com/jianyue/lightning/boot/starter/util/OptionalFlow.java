package com.jianyue.lightning.boot.starter.util;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class OptionalFlow<S> {


    /**
     * hold value
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<S> value;


    /**
     * @param value optional value
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public OptionalFlow(Optional<S> value) {
        this.value = value;
    }

    public OptionalFlow(S value) {
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
    public static <S> OptionalFlow<S> of(Optional<S> value) {
        return new OptionalFlow<>(value);
    }

    public static <S> OptionalFlow<S> of(S value) {
        return of(Optional.ofNullable(value));
    }

    public static <S> OptionalFlow<S> empty() {
        return new OptionalFlow<>(Optional.empty());
    }

    public Optional<S> getValue() {
        return this.value;
    }

    // 消费null / real value
    public OptionalFlow<S> consumeOrNull(Consumer<S> consumer) {
        if (this.value.isPresent()) {
            consumer.accept(this.value.get());
        } else {
            consumer.accept(null);
        }
        return this;
    }

    public OptionalFlow<S> consume(Consumer<S> consumer) {
        this.value.ifPresent(consumer);
        return this;
    }


    public OptionalFlow<S> consumeOrElse(Consumer<S> consumer, NOArgConsumer operation) {
        Objects.requireNonNull(consumer);
        Objects.requireNonNull(operation);
        if (this.value.isPresent()) {
            consumer.accept(this.value.get());
        } else {
            operation.accept();
        }

        return this;
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
    public OptionalFlow<S> orElse(Supplier<S> supplier) {
        return isPresent() ? this : OptionalFlow.of(supplier.get());
    }

    public OptionalFlow<S> orElse(S target) {
        return isPresent() ? this : OptionalFlow.of(target);
    }

    /**
     * 无参消费者
     *
     * @param consumer consumer
     */
    public OptionalFlow<S> orElse(NOArgConsumer consumer) {
        Objects.requireNonNull(consumer);
        if (this.value.isEmpty()) {
            consumer.accept();
        }
        return this;
    }


    /**
     * map
     *
     * @param function map函数
     * @param <T>      type
     * @return result
     */
    public <T> OptionalFlow<T> map(Function<S, T> function) {
        return new OptionalFlow<>(this.value.map(function));
    }


    /**
     * 统一返回结果!
     *
     * @return null Or Object
     * <p>
     * if return null , may not call ifPresent  or OrElse or return result is null!
     */
    @Nullable
    public S get() {
        return value.orElse(null);
    }

    public S getOrDefault(Supplier<S> targetSupplier) {
        return value.orElseGet(targetSupplier);
    }

    public S getOrDefault(S target) {
        return value.orElse(target);
    }
    
    
    // ------------------------------- if-else -------------------------------------------------------------------------
    /**
     * 根据之前的值和期待值进行比较
     * @param expectValue 期待值
     * @param tConsumer true
     * @param fConsumer false
     * @param <T> 类型
     * @return 表达式
     */
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue, @Nullable Consumer<T> tConsumer, @Nullable Consumer<T> fConsumer) {
        return value -> switchFunc((ele) -> value != null && value.equals(expectValue),tConsumer,fConsumer).accept(value);
    }
    // true operation / false operation
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue, @Nullable NOArgConsumer tConsumer, @Nullable NOArgConsumer fConsumer) {
        return value -> switchFunc((ele) -> value != null && value.equals(expectValue),tConsumer,fConsumer).accept(value);
    }
    // true consumer / false operation
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable Consumer<T> tConsumer,@Nullable NOArgConsumer fConsumer) {
        return value -> switchFunc((ele) -> value != null &&  value.equals(expectValue),tConsumer,fConsumer).accept(value);
    }
    // true consumer / false none
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable Consumer<T> tConsumer) {
        return value -> switchFunc(ele -> value != null && value.equals(expectValue),tConsumer,(NOArgConsumer)null).accept(value);
    }
    // true operation / false none
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable NOArgConsumer tConsumer) {
        return value -> switchFunc(ele -> value != null && value.equals(expectValue),tConsumer, null).accept(value);
    }
    // true operation / false consumer
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue, @Nullable NOArgConsumer tConsumer, @Nullable Consumer<T> fConsumer) {
        return value -> switchFunc((ele) -> value != null && value.equals(expectValue),tConsumer != null ? (ele) -> tConsumer.accept() : null,fConsumer).accept(value);
    }

    // true consumer / false consumer
    public static <T> Consumer<T> switchFunc(Predicate<T> predicate, @Nullable Consumer<T> tConsumer, @Nullable Consumer<T> fConsumer) {
        return value -> {
            if (predicate.test(value)) {
                if(tConsumer != null) {
                    tConsumer.accept(value);
                }
            }
            else {
                if(fConsumer != null) {
                    fConsumer.accept(value);
                }
            }
        };
    }
    // 根据条件消费..
    // pre -> true consumer / false operation
    public static <T> Consumer<T> switchFunc(Predicate<T> predicate,@Nullable Consumer<T> tConsumer,@Nullable NOArgConsumer fConsumer) {
        return switchFunc(predicate, tConsumer, t -> {
            if(fConsumer != null) {
                fConsumer.accept();
            }
        });
    }
    // pre -> true consumer / false none
    public static <T> Consumer<T> switchFunc(Predicate<T> predicate,@Nullable Consumer<T> tConsumer) {
        return switchFunc(predicate, tConsumer, t -> {});
    }
    // 根据条件map
    public static <T,R> Function<T,R> switchMapFunc(Predicate<T> predicate,Function<T,R> trFunction,Function<T,R> faFunction) {
        return value -> predicate.test(value) ? trFunction.apply(value) : faFunction.apply(value);
    }
    // equals map
    public static <T,R> Function<T,R> switchEqualsMapFunc(T expectValue,Function<T,R> trFunction,Function<T,R> faFunction) {
        return value -> value.equals(expectValue) ? trFunction.apply(value) : faFunction.apply(value);
    }
    // pre -> true operation / false  operation..
    public static <T> Consumer<T> switchFunc(Predicate<T> predicate, @Nullable NOArgConsumer tConsumer, @Nullable NOArgConsumer fConsumer) {
        return value -> {
            if (predicate.test(value)) {
                if(tConsumer != null) {
                    tConsumer.accept();
                }
            }
            else {
                if(fConsumer != null) {
                    fConsumer.accept();
                }
            }
        };
    }
    // 根据条件map
    public static <T,R> Function<T,R> switchMapFunc(Predicate<T> predicate, Function<T,R> trFunction, Supplier<R> fafunction) {
        return switchMapFunc(predicate,trFunction,t -> fafunction.get());
    }

    // 根据条件map
    public static <T,R> Function<T,R> switchMapFunc(Predicate<T> predicate, Supplier<R> trFunction, Supplier<R> fafunction) {
        return switchMapFunc(predicate,it -> trFunction.get(),t -> fafunction.get());
    }

    // 根据条件map
    public static <T,R> Function<T,R> switchMapFunc(Predicate<T> predicate, R trResult, R frResult) {
        return switchMapFunc(predicate,it -> trResult,t -> frResult);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OptionalFlow<?> that = (OptionalFlow<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this,value);
    }
}
