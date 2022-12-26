package com.jianyue.lightning.boot.starter.util.dataflow.impl;


import com.jianyue.lightning.boot.starter.util.dataflow.Context;

/**
 * @author FLJ
 * @date 2022/10/26
 * @time 17:30
 * @Description 捕捉异常的上下文
 *
 *
 * 你能够随意的增加想要捕获异常的处理器 ..
 * 理论上在任何处理器之前添加的异常 或者之后 添加的异常处理器 将在之后添加的或者之前添加的一个处理器发生错误进行生效 ...
 * 如果没有,则直接抛出异常,否则将清空异常处理器,这样能够在某一个处理器失败之后,捕捉相应特定的异常进行处理,从而让流程继续 ...
 *
 * 默认实现根据先增加处理器之后才可以增加异常处理器的形式 保证异常处理器 的顺序在处理器之后 ..
 *
 * 其他实现,可能存在在处理器之前增加异常处理器, 它同样可以是看作在处理器之后增加的 ...
 *
 *
 */
public interface CaughtExceptionContext<T,R> extends Context<T,R> {

    /**
     * 此异常处理器会 可以在任何处理器之前或者在处理器之间加入 ...  一旦加入将能够捕捉所有异常信息
     * 在上下文非lazy的情况下,则 所有的处理器将会等待存在exceptionHandler 之后 以及 start方法之前进行执行 ...
     * 同时可以多次添加 异常处理器 ... 将会被依次执行 ...
     *
     * @param exceptionHandler exceptionHandler ...
     * @return
     */
    CaughtExceptionContext<T,R> addCaughtExceptionHandler(CaughtDataFlowExceptionHandler<Context<T,R>> exceptionHandler);
}
