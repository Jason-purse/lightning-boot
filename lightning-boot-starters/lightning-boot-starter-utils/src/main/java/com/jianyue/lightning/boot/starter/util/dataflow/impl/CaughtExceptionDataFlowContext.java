package com.jianyue.lightning.boot.starter.util.dataflow.impl;

/**
 * @author FLJ
 * @date 2022/10/26
 * @time 13:12
 * @Description 能够捕获异常的数据流上下文
 */
public interface CaughtExceptionDataFlowContext<T> extends DataFlowContext<T> {

    /**
     * 添加异常捕捉
     *
     * 多次添加异常捕捉,将导致在同一上下文下 链式触发 ..
     *
     * // 同一上下文的意思是(相同类型的不同数据流会尝试根据泛型尽力解析而得出的可以在同一个上下文中进行设置切换,并没有重新开启一个新的上下文进行处理) ...
     * // 不同上下文就是泛型解析失败(进行新上下文创建) ...
     *
     * 对于使用了{@link SimpleDataFlowHandler} 以及 {@link DataFlowHandler} 匿名实现子类
     * 能够保证它是同一上下文(如果当前DataFlow 于 下一个DataFlow的类型相同) ..
     * 其他类型,例如泛型集合形式的DataFlowHandler(将无法解析泛型),因为泛型在运行时被擦除(例如 new DataFlowHandler<String>() ...,包含了自动泛型推断的处理器将解析失败) ... 将会导致创建不同上下文
     *
     * 并且懒惰模式下,同一个上下文的 此方法调用可以在handler 之前或者之后 ..
     * 但是非懒惰模式下,此方法调用必须在对应捕捉handler异常的{@link #addDataFlowHandler(DataFlowHandler)} 以及其他变种方法之前 ...
     *
     *
     * 注意, 此捕捉只能够捕捉同一上下文中的handler 异常 ..
     *
     * 从一个上下文切换到另一个上下文的情况下(也就是dataFlow 类型从一个类型切换到另一个类型的情况下(不同类型)则导致无法捕捉) ...
     *
     * 也就是说从一个上下文到另一个上下文 你必须 保证 不会存在异常发生,否则将无法处理 ...
     *
     *
     * @param consumer 出现异常的情况下回调
     * @param <S> 目标数据流类型
     * @return 新的数据流上下文
     */
    default <S extends T> DataFlowContext<S> addCaughtCallback(CaughtDataFlowConsumer<S> consumer) {
        return addDataFlowConsumerForSameRef(consumer);
    }
}
