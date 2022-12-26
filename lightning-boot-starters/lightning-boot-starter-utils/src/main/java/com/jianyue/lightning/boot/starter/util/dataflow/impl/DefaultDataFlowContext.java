package com.jianyue.lightning.boot.starter.util.dataflow.impl;

import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.function.Supplier;

/**
 * @author FLJ
 * @date 2022/10/26
 * @time 9:28
 * @Description 默认的数据流上下文
 * <p>
 * <p>
 * 用来改变现有的 方法过长的问题,以及各种传参问题
 */
public class DefaultDataFlowContext<T> implements DataFlowContext<T>, CaughtExceptionDataFlowContext<T> {

    /**
     * data flow
     */
    @Nullable
    private T dataFlow;

    private Supplier<T> supplier;

    @Nullable
    private CaughtDataFlowConsumer<T> exceptionConsumer;

    public DefaultDataFlowContext(@Nullable T dataFlow) {
        this.supplier = () -> dataFlow;
        this.dataFlow = dataFlow;
    }


    public DefaultDataFlowContext(@NonNull Supplier<T> dataFlowSupplier) {
        // 构建时直接执行 ...
        this.supplier = dataFlowSupplier;
    }

    @Nullable
    @Override
    public T getDataFlow() {

        if (dataFlow == null) {
            if (supplier != null) {
                dataFlow = supplier.get();
                supplier = null;
            }
        }

        return dataFlow;

    }

    @Override
    public DefaultDataFlowContext<T> setDataFlow(@Nullable T t) {

        this.dataFlow = t;

        // 把 supplier 置空
        this.supplier = null;

        return this;
    }


    @Override
    @SuppressWarnings("unchecked")
    public <S> DefaultDataFlowContext<S> addDataFlowHandler(DataFlowHandler<T, S> handler) {
        DefaultDataFlowContext<T> context = this;

        if (handler instanceof CaughtDataFlowConsumer) {
            CaughtDataFlowConsumer<T> rawHandler = (CaughtDataFlowConsumer<T>) handler;
            // 需要额外处理为handler
            if (rawHandler.isConsumer()) {
                addHandler0(rawHandler, context);
            }
            return caughtDataFlowException(rawHandler, (DefaultDataFlowContext<S>) context);
        }

        return addHandler0(handler, context);

    }

    /**
     * 真正增加handler 的方法
     *
     * @param handler 目标handler
     * @param context 当前上下文
     * @param <S>     目标数据流类型
     * @return 携带目标数据流类型的上下文
     */
    private <S> DefaultDataFlowContext<S> addHandler0(DataFlowHandler<T, S> handler, DefaultDataFlowContext<T> context) {
        CaughtDataFlowConsumer<T> exceptionConsumer = this.exceptionConsumer;
        if (handler.isLazy()) {
            return lazyExecute(getHandler(handler, exceptionConsumer), handler, context);
        }
        return nonLazyExecute(getHandler(handler, exceptionConsumer), handler, context);
    }

    // 捕捉数据流异常
    private <S> DefaultDataFlowContext<S> caughtDataFlowException(CaughtDataFlowConsumer<T> handler, DefaultDataFlowContext<S> context) {
        if (this.exceptionConsumer != null) {
            CaughtDataFlowConsumer<T> rawConsumer = this.exceptionConsumer;

            this.exceptionConsumer = new CaughtDataFlowConsumer<T>() {
                @Override
                public void exceptionCaught(DataFlowContext<T> context, Exception e) {
                    rawConsumer.exceptionCaught(context, e);
                    handler.exceptionCaught(context, e);
                }
            };
        } else {
            this.exceptionConsumer = handler;
        }
        return context;
    }

    /**
     * 包装handler ..
     *
     * @param handler           target handler
     * @param exceptionConsumer 异常消费器
     * @param <S>               目标数据流类型
     * @return 数据流处理器
     */
    @NonNull
    @SuppressWarnings("unchecked")
    private <S> DataFlowHandler<T, S> getHandler(DataFlowHandler<T, S> handler, CaughtDataFlowConsumer<T> exceptionConsumer) {
        DefaultDataFlowContext<T> rawContext = this;
        return context -> {
            try {
                return handler.executeForDataFlow(context);
            } catch (Exception e) {
                // 执行失败 ...
                if (exceptionConsumer != null) {
                    exceptionConsumer.exceptionCaught(context, e);
                    return (S) rawContext;
                } else {
                    throw e;
                }
            }
        };
    }


    @SuppressWarnings("unchecked")
    private <S> DefaultDataFlowContext<S> lazyExecute(DataFlowHandler<T, S> proxyHandler, DataFlowHandler<T, S> handler, DefaultDataFlowContext<T> context) {
        CaughtDataFlowConsumer<T> exceptionConsumer = this.exceptionConsumer;
        // 置为 null,一旦设置 ...
        this.exceptionConsumer = null;
        // 如果是 同类型的data flow
        if (sameTypeCheck(handler.getClass(), handler)) {
            // 如果为空的情况下,则表示第一次增加 ...
            if(supplier == null) {
                // 设置一个supplier ..
                supplier = () -> {
                    Tuple<S, Exception> tuple = doExecuteForDataFlow(proxyHandler, context,exceptionConsumer);
                    if(tuple.getFirst() != null) {
                        setDataFlow((T)tuple.getFirst());
                    }

                    //否则没必要设置
                    return (T) context;
                };
            }
            else {
                // 最后一层一层的调用 ...
                supplier = () -> {
                    // 将计算累计
                    context.setDataFlow(supplier.get());
                    // 也要尝试这样调用 ...
                    Tuple<S, Exception> tuple =  doExecuteForDataFlow(proxyHandler,context,exceptionConsumer);
                    if(tuple.getFirst() != null) {
                        setDataFlow((T)tuple.getFirst());
                    }

                    //否则没必要设置
                    return (T) context;
                };
            }
            dataFlow = null;
            return (DefaultDataFlowContext<S>) context;
        }

        // 这种情况下,直接给一个新的上下文(装饰)
        return new DefaultDataFlowContext<>(() -> proxyHandler.executeForDataFlow(context));
    }


    @NonNull
    @SuppressWarnings("unchecked")
    private <S> DefaultDataFlowContext<S> nonLazyExecute(DataFlowHandler<T, S> proxyHandler, DataFlowHandler<T, S> handler, DefaultDataFlowContext<T> context) {
        CaughtDataFlowConsumer<T> exceptionConsumer = this.exceptionConsumer;
        this.exceptionConsumer = null;
        Tuple<S,Exception> dataFlow = doExecuteForDataFlow(proxyHandler, context,exceptionConsumer);

        if (dataFlow.getFirst() != null) {
            if (sameTypeCheck(handler.getClass(), handler)) {
                setDataFlow((T) dataFlow);
                // 否则直接返回就可以
                return (DefaultDataFlowContext<S>) this;
            }
            // 否则直接返回
            return new DefaultDataFlowContext<>(dataFlow.getFirst());
        }

        // 否则表示报错了 ..
        // 这种处于下一个上下文的情况下,无法处理 ...
        // 直接报错就行
        throw new RuntimeException(dataFlow.getSecond());
    }

    private <S> Tuple<S, Exception> doExecuteForDataFlow(DataFlowHandler<T, S> proxyHandler, DefaultDataFlowContext<T> context, CaughtDataFlowConsumer<T> exceptionConsumer) {
        try {
            getDataFlow();
            return new Tuple<>(proxyHandler.executeForDataFlow(context), null);
        } catch (Exception e) {
            if (exceptionConsumer != null) {
                // 处理的是handler中的异常 ...
                exceptionConsumer.exceptionCaught(context, e);
            }
            return new Tuple<>(null,e);
        }
    }


    /**
     * 相同类型检查
     */
    @SuppressWarnings("rawtypes")
    private <S> boolean sameTypeCheck(Class<? extends DataFlowHandler> handlerClass, DataFlowHandler<T, S> handler) {

        if (SimpleDataFlowHandler.class.isAssignableFrom(handlerClass)) {

            SimpleDataFlowHandler rawHandler = (SimpleDataFlowHandler) handler;
            // 表示同一个类
            return rawHandler.getCurrentDataFlowType() == rawHandler.getTargetDataFlowType();
        }

        // 使用ResolvableType
        ResolvableType generic = ResolvableType.forType(handlerClass.getGenericInterfaces()[0]);

        // 表示无法解析
        if (generic.getGeneric(0).toString().equals("?") || generic.getGeneric(1).toString().equals("?")) {
            return false;
        } else return generic.getGeneric(0).getType() == generic.getGeneric(1).getType();
    }

    @Override
    public String toString() {
        return getClass().getName() + "[dataflow: " + (isPending() ? "pending" : dataFlow) + "]";
    }

    private boolean isPending() {
        if (dataFlow == null) {
            return supplier != null;
        }
        return false;
    }

    // 创建一个上下文
    public static <R> DefaultDataFlowContext<R> of(R dataFlow) {
        return new DefaultDataFlowContext<>(dataFlow);
    }


    // 创建一个上下文,并捕捉 初始化过程的异常
    public static <T> DefaultDataFlowContext<T> of(Supplier<T> dataFlowSupplier, SupplierExceptionConsumer<T> exceptionConsumer) {
        try {
            DefaultDataFlowContext<T> tDefaultDataFlowContext = new DefaultDataFlowContext<>(dataFlowSupplier);
            tDefaultDataFlowContext.getDataFlow();
            return tDefaultDataFlowContext;
        } catch (Exception e) {
            return of(exceptionConsumer.exceptionHandle(e));
        }
    }

    /**
     * supplier 爆发异常的 异常消费器,并产生一个新的 dataFlow
     *
     * @param <T> 当前数据流类型
     */
    public static abstract class SupplierExceptionConsumer<T> {

        public abstract T exceptionHandle(Exception e);
    }
}
