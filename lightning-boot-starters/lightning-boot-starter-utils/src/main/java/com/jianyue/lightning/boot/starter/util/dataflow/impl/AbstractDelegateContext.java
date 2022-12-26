package com.jianyue.lightning.boot.starter.util.dataflow.impl;


import com.jianyue.lightning.boot.starter.util.dataflow.Context;

/**
 * @author FLJ
 * @date 2022/12/9
 * @time 12:57
 * @Description DELEGATE 是context
 */
public abstract class AbstractDelegateContext<T,R> implements Context<T,R> {

    public AbstractDelegateContext(Context<T,R> delegate) {
        this.delegate = delegate;
    }

    private final Context<T,R> delegate;

    @Override
    public T getDataFlow() {
        return delegate.getDataFlow();
    }

    @Override
    public Context<T, R> setDataFlow(T dataFlow) {
        return delegate.setDataFlow(dataFlow);
    }

    @Override
    public R getResult() {
        return delegate.getResult();
    }

    @Override
    public void setResult(R result) {
        delegate.setResult(result);
    }

    @Override
    public Context<T, R> start() {
        return delegate.start();
    }

    @Override
    public CaughtExceptionContext<T, R> addDataFlowHandler(DataFlowHandler<Context<T, R>> dataFlowHandler) {
        return delegate.addDataFlowHandler(dataFlowHandler);
    }

    @Override
    public Context<T, R> catchAll(CatchAllExceptionHandler<Context<T, R>> catchAllExceptionHandler) {
        return delegate.catchAll(catchAllExceptionHandler);
    }
}
