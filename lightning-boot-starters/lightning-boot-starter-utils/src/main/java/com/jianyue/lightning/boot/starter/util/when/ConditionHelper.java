package com.jianyue.lightning.boot.starter.util.when;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author JASONJ
 * @date 2022/1/24
 * @time 7:21
 * @description when 帮助器..
 **/
public class ConditionHelper {

    // if (根据它  引导出if/else  / if/elseif/else)
    public  static   OncePredicate first(Supplier<Boolean> predicate, Operation operation) {
        return OncePredicate.of(predicate,operation);
    }

    public static OncePredicate first(Boolean predicate, Operation operation) {
        return first(() -> predicate,operation);
    }

    public static <T> Optional<T> first(Boolean predicate, Supplier<T> supplier) {
        if(predicate != null && predicate) {
            return Optional.ofNullable(supplier.get());
        }
        return Optional.empty();
    }

    public static <T> OncePredicate firsted(Supplier<Boolean> predicate, Supplier<T> supplier) {
        return OncePredicate.of(predicate,supplier);
    }

    public static <T> OncePredicate firsted(Boolean predicate,Supplier<T> supplier) {
        return firsted(() -> predicate,supplier);
    }
}
