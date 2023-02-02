package com.jianyue.lightning.boot.starter.util.dataflow.impl;


import com.jianyue.lightning.boot.starter.util.dataflow.Context;

/**
 * @author FLJ
 * @date 2022/12/8
 * @time 17:05
 * @Description 仅仅输入的上下文 ...
 */
public interface InputContext<T> extends Context<T, Void> {

    /**
     * 返回 inputContext
     * @param t dataflow
     * @param <T> dataflow type
     * @return context
     */
    static <T> InputContext<T> of(T t) {
        return new DefaultInputContext<>(Context.of(t));
    }
}

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 11:45
 * @Description 代理到目标实现
 */
class DefaultInputContext<T> extends AbstractDelegateContext<T,Void> implements  InputContext<T> {

    public DefaultInputContext(Context<T,Void> delegate) {
        super(delegate);
    }
}
