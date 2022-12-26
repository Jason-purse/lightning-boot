package com.jianyue.lightning.boot.starter.util.dataflow.impl;

/**
 * @author FLJ
 * @date 2022/10/26
 * @time 9:14
 * @Description 业务处理上下文
 *
 * 实现类应该同时继承
 */
public interface DataFlowContext<T> {

    /**
     * 获取 dataFlow
     * @return dataflow object
     *
     * 它可能会存在抛出异常的情况,例如 {@link DefaultDataFlowContext} 初始构建 可以提供的DataFlow  可以是一个 Supplier ...
     * @throws Exception
     */
    T getDataFlow();

    /**
     * 设置 dataFlow
     * 此操作很危险, 当设置dataFlow时,会立即终端所有懒惰处理模式下的所有已经增加的 FlowHandler ..
     * 要想安全的设置,在懒惰模式下请先调用getDataFlow()之后,在尝试设置 DataFlow ...(用此方法设置) ...
     * @param t dataflow object
     */
    DataFlowContext<T> setDataFlow(T t);


    /**
     * 产生一个新的DataFlowContext
     * @param handler 处理当前数据流的处理器
     * @param <S> 目标数据流类型
     * @return 新的数据流上下文
     */
    <S> CaughtExceptionDataFlowContext<S> addDataFlowHandler(DataFlowHandler<T,S> handler);

    /**
     * 仅仅做同一种 类型的 DataFlow 消费 ...
     */
    @SuppressWarnings("unchecked")
    default <S extends T> CaughtExceptionDataFlowContext<S> addDataFlowConsumerForSameType(SameTypeDataFlowHandler<S> consumer) {
        return (CaughtExceptionDataFlowContext<S>) addDataFlowHandler(((DataFlowHandler<T,S>) consumer));
    }

    /**
     * 仅仅做同一个DataFlow Ref 引用消费
     *
     * 注意到
     * @see CaughtDataFlowConsumer 实现至  SameRefDataFlowConsumer, 所以 如果使用 CaughtDataFlowConsumer,需要覆盖实现方法
     * {@link CaughtDataFlowConsumer#internalExecuteForDataFlow(DataFlowContext)}
     *
     * 并且当 {@link CaughtDataFlowConsumer} 做消费者的时候,如果 上述方法{@link CaughtDataFlowConsumer#internalExecuteForDataFlow(DataFlowContext)} 抛出了一个异常
     * 则不能够在自己的 {@link CaughtDataFlowConsumer#exceptionCaught(DataFlowContext, Exception)} 方法中接收到异常处理 ...
     */
    @SuppressWarnings("unchecked")
    default <S extends T> CaughtExceptionDataFlowContext<S> addDataFlowConsumerForSameRef(SameRefDataFlowConsumer<S> consumer) {
        return (CaughtExceptionDataFlowContext<S>) addDataFlowHandler((DataFlowHandler<T,S>) consumer);
    }


    /**
     * 用于处理当前上下文,并推动下一个上下文的产生 ..
     * @param <S>  当前上下文的数据流类型
     * @param <T> 目标上下文的数据流类型
     */
    interface DataFlowHandler<S,T> {

        /**
         * 执行并返回下一步的数据流
         * @return next dataflow object
         */
        T executeForDataFlow(DataFlowContext<S> context);

        /**
         * 是否懒惰执行 ..
         * @return 表示仅仅在 getDataFlow时 进行数据流的移动
         */
        default boolean isLazy() {
            return false;
        }
    }

    /**
     * 直接指定 T,S 类型的 处理器(以防类似于 泛型集合/数组 无法解析出泛型 type argument) ...
     * @param <S> current data flow type s
     * @param <T> next data flow type t
     */
    interface SimpleDataFlowHandler<S,T> extends DataFlowHandler<S,T> {

        Class<T> getTargetDataFlowType();

        Class<S> getCurrentDataFlowType();
    }

    /**
     * 相同data flow type 处理器
     * @param <S> current and next flow type
     */
    interface SameTypeDataFlowHandler<S> extends DataFlowHandler<S,S> {

        @Override
        S executeForDataFlow(DataFlowContext<S> context);

    }

    /**
     * 整个大环境仅仅使用一个 DataFlow ...
     * @param <S> forever is this data flow
     */
    interface SameRefDataFlowConsumer<S> extends DataFlowHandler<S,S> {

        @Override
        default S executeForDataFlow(DataFlowContext<S> context) {
            internalExecuteForDataFlow(context);
            return context.getDataFlow();
        }

        void internalExecuteForDataFlow(DataFlowContext<S> context);

        /**
         * 默认是可以作为消费者handler 处理的,这是为了处理特殊的Caught...Consumer ..
         *
         * {@link CaughtDataFlowConsumer} 的此方法默认为false,如果希望它作为正常的 {@link SameRefDataFlowConsumer} 使用,覆盖为true ...
         *
         * @return true / false
         */
        default boolean isConsumer() {
            return true;
        }
    }

    /**
     * 异常捕捉(捕捉的肯定是 当前handler 所抛出的异常) ...
     * // 那么使用 sameRef 形式的消费器
     * @param <S> current or next data flow type
     */
    interface CaughtDataFlowConsumer<S> extends SameRefDataFlowConsumer<S> {

        /**
         * 作为正常消费者使用时, {@link #isConsumer()} must override ..
         * @param context
         */
        @Override
        default void internalExecuteForDataFlow(DataFlowContext<S> context) {
            // pass
            // no execute
        }

        @Override
        default boolean isConsumer() {
            return false;
        }

        /**
         * 异常信息
         * 该方法和 {@link #isConsumer()} and {@link #internalExecuteForDataFlow(DataFlowContext)} 同时互斥
         * @param context 上下文
         * @param e 异常信息
         */
        default void exceptionCaught(DataFlowContext<S> context,Exception e) {
            // pass
        }

    }
}
