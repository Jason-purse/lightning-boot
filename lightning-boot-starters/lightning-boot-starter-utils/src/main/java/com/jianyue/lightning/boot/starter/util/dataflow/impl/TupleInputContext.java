package com.jianyue.lightning.boot.starter.util.dataflow.impl;


import com.jianyue.lightning.boot.starter.util.dataflow.Context;

/**
 * @author FLJ
 * @date 2022/12/8
 * @time 23:26
 * @since 2022/12/8
 * <p>
 * 包含元组的上下文
 **/
public interface TupleInputContext<S, V> extends InputContext<Tuple<S, V>> {

    static <S, V> TupleInputContext<S, V> of(S s, V v) {
        return new DefaultTupleInputContext<>(InputContext.of(new Tuple<>(s, v)));
    }
}

class DefaultTupleInputContext<S, V> extends DefaultInputContext<Tuple<S, V>> implements TupleInputContext<S, V> {

    public DefaultTupleInputContext(Context<Tuple<S, V>, Void> context) {
        super(context);
    }

}
