package com.jianyue.lightning.boot.starter.util.when;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author JASONJ
 * @date 2022/1/24
 * @time 7:55
 * @description 多个else if 结合体..
 **/
public interface MorePredicate {

    MorePredicate any(Supplier<Boolean> predicate, Operation operation);

    default MorePredicate any(Boolean predicate, Operation operation) {
        return any(() -> predicate,operation);
    }

    Optional<?> any(Supplier<Boolean> predicate,Supplier<?> supplier);

    default  Optional<?> any(Boolean predicate,Supplier<?> supplier) {
        return any(() -> predicate,supplier);
    }

    MorePredicate anyed(Supplier<Boolean> predicate,Supplier<?> supplier);

    default  MorePredicate anyed(Boolean predicate,Supplier<?> supplier) {
        return anyed(() -> predicate,supplier);
    }

    void not(Operation operation);

    Optional<?> noted(Supplier<?> supplier);

    default  Optional<?> noted(Object value) {
        return noted(() -> value);
    }

    /**
     * 开启一个if /else 条件实体
     * @param associatePredicate associatePredicate
     * @param predicate predicate
     * @param operation operation
     * @return  MorePredicate
     */
    static MorePredicate of(OncePredicate associatePredicate, Supplier<Boolean> predicate, Operation operation) {
        return new DefaultMorePredicateImpl(associatePredicate,predicate,operation);
    }

    /**
     * 开启一个 if/else 条件实体
     * @param associatePredicate  associatePredicate
     * @param predicate predicate
     * @param supplier supplier
     * @return MorePredicate
     */
    static MorePredicate of(OncePredicate associatePredicate,Supplier<Boolean> predicate,Supplier<?> supplier) {
        return new DefaultMorePredicateImpl(associatePredicate,predicate,supplier);
    }
}

class DefaultMorePredicateImpl implements MorePredicate {
    /**
     * 条件标识 为true 表示已经完成 false 表示未完成..
     */
    private Boolean conditionFinishFlag = Boolean.FALSE;

    private final OncePredicate associatePredicate;

    public DefaultMorePredicateImpl(OncePredicate associatePredicate, Supplier<Boolean> predicate, Operation operation) {
        this(associatePredicate,predicate,() -> {
            operation.exec();
            return null;
        });
    }
    public DefaultMorePredicateImpl(OncePredicate associatePredicate, Supplier<Boolean> predicate, Supplier<?> supplier) {
        if (!associatePredicate.getConditionFinishFlag()) {
            Boolean aBoolean = predicate.get();
            if (aBoolean != null && aBoolean) {
                this.conditionFinishFlag = true;
                associatePredicate.setTarget(supplier.get());
            }
        }
        this.associatePredicate = associatePredicate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<?> any(Supplier<Boolean> predicate, Supplier<?> supplier) {
        if(!this.conditionFinishFlag) {
            Boolean aBoolean = predicate.get();
            if(aBoolean != null && aBoolean) {
                this.conditionFinishFlag = true;
                this.associatePredicate.setTarget(supplier.get());
            }
        }
        return Optional.ofNullable(this.associatePredicate.getTarget());
    }

    @Override
    public MorePredicate anyed(Supplier<Boolean> predicate, Supplier<?> supplier) {
        if(!this.conditionFinishFlag) {
            Boolean aBoolean = predicate.get();
            if(aBoolean != null && aBoolean) {
                this.conditionFinishFlag = true;
                this.associatePredicate.setTarget(supplier.get());
            }
        }
        return this;
    }

    @Override
    public MorePredicate any(Supplier<Boolean> supplier, Operation operation) {
        // 执行..
        if(!this.conditionFinishFlag) {
            Boolean aBoolean = supplier.get();
            if(aBoolean != null && aBoolean) {
                this.conditionFinishFlag = true;
                operation.exec();
            }
        }
        return this;
    }

    @Override
    public void not(Operation operation) {
        // 执行...
        // 如果前置 if 条件为true,那么这里的Else 必然不执行...
        if(!this.conditionFinishFlag && !this.associatePredicate.getConditionFinishFlag()) {
            operation.exec();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<?> noted(Supplier<?> supplier) {
        if(!this.conditionFinishFlag && !this.associatePredicate.getConditionFinishFlag()) {
            this.associatePredicate.setTarget(supplier.get());
        }
        return Optional.ofNullable(this.associatePredicate.getTarget());
    }

}
