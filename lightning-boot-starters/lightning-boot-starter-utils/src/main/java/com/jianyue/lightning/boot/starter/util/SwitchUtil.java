package com.jianyue.lightning.boot.starter.util;


import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author FLJ
 * @dateTime 2022/1/12 16:56
 * @description 消除 if 的使用 和OptionalFlux 结合使用效果更佳..
 */
public class SwitchUtil {
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
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable Operation tConsumer,@Nullable Operation fConsumer) {
        return value -> switchFunc((ele) -> value != null && value.equals(expectValue),tConsumer,fConsumer).accept(value);
    }
    // true consumer / false operation
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable Consumer<T> tConsumer,@Nullable Operation fConsumer) {
        return value -> switchFunc((ele) -> value != null &&  value.equals(expectValue),tConsumer,fConsumer).accept(value);
    }
    // true consumer / false none
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable Consumer<T> tConsumer) {
        return value -> switchFunc(ele -> value != null && value.equals(expectValue),tConsumer,(Operation)null).accept(value);
    }
    // true operation / false none
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable Operation tConsumer) {
        return value -> switchFunc(ele -> value != null && value.equals(expectValue),tConsumer, null).accept(value);
    }
    // true operation / false consumer
    public static <T> Consumer<T> switchEqualsFunc(@Nullable T expectValue,@Nullable Operation tConsumer,@Nullable Consumer<T> fConsumer) {
        return value -> switchFunc((ele) -> value != null && value.equals(expectValue),tConsumer != null ? (ele) -> tConsumer.exec() : null,fConsumer).accept(value);
    }

    // true consumer / false consumer
    public static <T> Consumer<T> switchFunc(Predicate<T> predicate,@Nullable Consumer<T> tConsumer,@Nullable Consumer<T> fConsumer) {
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
    public static <T> Consumer<T> switchFunc(Predicate<T> predicate,@Nullable Consumer<T> tConsumer,@Nullable Operation fConsumer) {
        return switchFunc(predicate, tConsumer, t -> {
            if(fConsumer != null) {
                fConsumer.exec();
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
    public static <T> Consumer<T> switchFunc(Predicate<T> predicate,@Nullable Operation tConsumer,@Nullable Operation fConsumer) {
        return value -> {
            if (predicate.test(value)) {
                if(tConsumer != null) {
                    tConsumer.exec();
                }
            }
            else {
                if(fConsumer != null) {
                    fConsumer.exec();
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

    // 无参消费者..
    @FunctionalInterface
    public interface Operation {
        void exec();
    }
}
