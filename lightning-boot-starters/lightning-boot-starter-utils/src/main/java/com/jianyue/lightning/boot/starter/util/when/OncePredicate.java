package com.jianyue.lightning.boot.starter.util.when;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author JASONJ
 * @date 2022/1/24
 * @time 7:21
 * @description 一次性条件
 **/
public interface OncePredicate {

    static <T> OncePredicate of(Supplier<Boolean> predicate, Operation operation) {
        return new DefaultOncePredicateImpl(predicate, operation);
    }

    static <T> OncePredicate of(Supplier<Boolean> predicate, Supplier<T> supplier) {
        return new DefaultOncePredicateImpl(predicate, supplier);
    }

    void not(Operation operation);

    <T> Optional<T> noted(Supplier<T> supplier);

    default <T> Optional<T> noted(T value) {
        return noted(() -> (T) value);
    }

    MorePredicate any(Supplier<Boolean> predicate, Operation operation);

    default MorePredicate any(Boolean predicate, Operation operation) {
        return any(() -> predicate, operation);
    }

    MorePredicate anyed(Supplier<Boolean> predicate, Supplier<?> supplier);

    default MorePredicate anyed(Boolean predicate, Supplier<?> supplier) {
        return anyed(() -> predicate, supplier);
    }

    <T> Optional<T> any(Supplier<Boolean> predicate, Supplier<T> supplier);

    default <T> Optional<T> any(Boolean predicate, Supplier<T> supplier) {
        return any(() -> predicate, supplier);
    }

    /**
     * 获取条件完成标志
     *
     * @return flag
     */
    Boolean getConditionFinishFlag();

    /**
     * 设置目标对象结果
     *
     * @param object 对象
     */
    void setTarget(Object object);

    /**
     * 获取目标对象
     *
     * @return 对象instance
     */
    Object getTarget();
}

/**
 * 单个条件的默认实现
 */
class DefaultOncePredicateImpl implements OncePredicate {
    /**
     * 条件是否执行完毕..
     */
    private Boolean conditionFinishFlag = Boolean.FALSE;

    private Object object;

    public DefaultOncePredicateImpl(Supplier<Boolean> predicate, Operation operation) {
        this(predicate, () -> {
            operation.exec();
            return null;
        });
    }


    public DefaultOncePredicateImpl(Supplier<Boolean> predicate, Supplier<?> supplier) {
        Boolean aBoolean = predicate.get();
        if (aBoolean != null && aBoolean) {
            this.conditionFinishFlag = true;
            this.object = supplier.get();
        }
    }


    @Override
    public MorePredicate any(Supplier<Boolean> predicate, Operation operation) {
        return MorePredicate.of(this, predicate, operation);
    }

    @Override
    public MorePredicate anyed(Supplier<Boolean> predicate, Supplier<?> supplier) {
        return MorePredicate.of(this, predicate, supplier);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> any(Supplier<Boolean> predicate, Supplier<T> supplier) {
        if (!this.conditionFinishFlag) {
            Boolean aBoolean = predicate.get();
            if (aBoolean != null && aBoolean) {
                this.object = supplier.get();
            }
        }
        return Optional.ofNullable((T) object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> noted(Supplier<T> supplier) {
        if (!conditionFinishFlag) {
            this.object = supplier.get();
        }
        return Optional.ofNullable((T) object);
    }


    @Override
    public void not(Operation operation) {
        if (!conditionFinishFlag) {
            operation.exec();
        }
    }

    @Override
    public void setTarget(Object object) {
        this.object = object;
    }

    @Override
    public Object getTarget() {
        return object;
    }

    @Override
    public Boolean getConditionFinishFlag() {
        return conditionFinishFlag;
    }
}