package com.jianyue.lightning.boot.starter.util.dataflow;

import com.jianyue.lightning.boot.starter.util.dataflow.impl.CaughtExceptionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 上下文对象
 * 标记接口
 */
public interface Context<T, R> {

    // 默认 false
    default boolean isLazy() {
        return false;
    }

    T getDataFlow();


    Context<T, R> setDataFlow(T dataFlow);

    /**
     * 一个结果
     */
    R getResult();

    void setResult(R result);

    // 开始 执行
    // lazy 情况下,就是等待这个方法调用处理 ..
    // 非lazy 情况下,增加handler的情况下直接处理了 ..
    Context<T, R> start();

    CaughtExceptionContext<T, R> addDataFlowHandler(DataFlowHandler<Context<T, R>> dataFlowHandler);

    /**
     * 强转为对应的能够捕获异常的 上下文
     *
     * @return 上下文
     */
    default CaughtExceptionContext<T, R> asCaughtExContext() {
        return (CaughtExceptionContext<T, R>) this;
    }

    /**
     * 全局处理 ...
     *
     * @return
     */
    Context<T, R> catchAll(CatchAllExceptionHandler<Context<T,R>> catchAllExceptionHandler);


    // 捕捉异常
    interface CaughtDataFlowExceptionHandler<T> {
        void handleException(T context, Exception e);
    }

    // 数据流处理器
    interface DataFlowHandler<T> {
        // 处理数据流
        void doHandle(T context);
    }

    /**
     * 全局异常处理
     * <p>
     * 兜底操作,如果 其他dataflow 异常已经被处理,则不会通知它 ...
     */
    interface CatchAllExceptionHandler<T> {
        void handleException(T context,Exception e);
    }


    // 创建一个初始数据流的 Context
    static <T, R> Context<T, R> of(T in) {
        return new InternalContext<>(in);
    }

    // 创建一个空的 flowin Context
    static <T,R> Context<T,R> of() {
        return new InternalContext<>(null);
    }

    static <T, R> Context<T, R> of(T in, boolean isLazy, R out) {
        return new InternalContext<>(in, isLazy, out);
    }

    static <T, R> Context<T, R> of(T in, boolean isLazy) {
        return new InternalContext<>(in, isLazy);
    }

}

@Slf4j
class InternalContext<T, R> implements Context<T, R>, CaughtExceptionContext<T, R> {

    private final boolean lazyFlag;

    private boolean isPending = false;

    private List<DataFlowHandler<Context<T, R>>> handlerList;

    private R result;

    private T dataFlow;

    private Exception exception;
    // 异常处理器
    private CaughtDataFlowExceptionHandler<Context<T,R>> exceptionHandler;

    private CatchAllExceptionHandler<Context<T,R>> catchAllExceptionHandler;

    /**
     * 上一个handler
     */
    @Nullable
    private DataFlowHandler<Context<T, R>> lastHandler;


    public InternalContext(T in, boolean isLazy, R out) {
        this.dataFlow = in;
        this.lazyFlag = isLazy;
        this.result = out;
        if (isLazy) {
            this.isPending = true;
        }
        this.handlerList = new ArrayList<>();
        log.debug("initialize an  InternalContext, mode is {} !!!", lazyFlag ? "lazy" : "immediate");
    }

    public InternalContext(T in, boolean isLazy) {
        this(in, isLazy, null);
    }

    public InternalContext(T in) {
        this(in, true);
    }

    @Override
    public T getDataFlow() {
        return dataFlow;
    }

    @Override
    public Context<T, R> setDataFlow(T dataFlow) {
        this.dataFlow = dataFlow;
        return this;
    }

    @Override
    public R getResult() {
        return result;
    }

    @Override
    public void setResult(R result) {
        this.result = result;
    }


    @Override
    public CaughtExceptionContext<T, R> addDataFlowHandler(DataFlowHandler<Context<T, R>> dataFlowHandler) {
        if (lazyFlag) {
            this.isPending = true;

            // 处理 异常处理器增加在 dataFlowHandler 之前的情况 ..
            CaughtDataFlowExceptionHandler<Context<T,R>> exceptionHandler = this.exceptionHandler;
            if (exceptionHandler != null) {
                this.exceptionHandler = null;
                ProxyDataFlowHandler<Context<T, R>> contextProxyDataFlowHandler = new ProxyDataFlowHandler<>(dataFlowHandler,this);
                lastHandler = contextProxyDataFlowHandler;
                contextProxyDataFlowHandler.addExceptionHandler(exceptionHandler);
            } else {
                lastHandler = new ProxyDataFlowHandler<>(dataFlowHandler, this);
                // 表示当前 handler 位置
            }
            handlerList.add(lastHandler);

        } else {
            // 否则直接丢弃,因为存在问题 ... 准备抛出错误(在start 调用时)
            if (exceptionHandler != null) {
                // 此时还没有异常,正常执行 ...
                // 如果有异常,则表示之前的异常处理器已经处理过异常,而被清理了 ...
                if (exception == null) {
                    List<DataFlowHandler<Context<T, R>>> handlerList = this.handlerList;
                    this.handlerList = new LinkedList<>();
                    for (DataFlowHandler<Context<T, R>> contextDataFlowHandler : handlerList) {
                        // 存在异常 ...
                        if (!doHandler(contextDataFlowHandler,this.exceptionHandler)) {
                            break;
                        }
                    }
                }
            }


            handlerList.add((lastHandler = new ProxyDataFlowHandler<>(dataFlowHandler,this)));
        }
        return this;
    }

    @Override
    public Context<T, R> catchAll(CatchAllExceptionHandler<Context<T,R>> catchAllExceptionHandler) {
        if (catchAllExceptionHandler != null) {

            if(this.catchAllExceptionHandler != null) {
                CatchAllExceptionHandler<Context<T,R>> catchAllExceptionHandler1 = this.catchAllExceptionHandler;
                this.catchAllExceptionHandler = (context, e) -> {
                    catchAllExceptionHandler1.handleException(context,e);
                    catchAllExceptionHandler.handleException(context,e);
                };
            }
            else {
                this.catchAllExceptionHandler = catchAllExceptionHandler;
            }

        }
        return this;
    }

    private boolean doHandler(DataFlowHandler<Context<T, R>> dataFlowHandler,CaughtDataFlowExceptionHandler<Context<T,R>> exceptionHandler) {
        try {
            dataFlowHandler.doHandle(this);
        } catch (Exception e) {
            if (exceptionHandler != null) {
                exceptionHandler.handleException(this,e);
                if(!isLazy()) {
                    // 表示当前为非Lazy 需要实时清除掉异常处理器 ..
                    this.exceptionHandler = null;
                }
            } else {
                // 必须记录异常
                exception = e;
                return false;
            }
        }
        return true;
    }

    @Override
    public CaughtExceptionContext<T, R> addCaughtExceptionHandler(CaughtDataFlowExceptionHandler<Context<T,R>> exceptionHandler) {
        CaughtDataFlowExceptionHandler<Context<T,R>> exceptionHandler1 = this.exceptionHandler;
        if (exceptionHandler1 != null) {
            this.exceptionHandler = (context,exception) -> {
                exceptionHandler1.handleException(context,exception);
                exceptionHandler.handleException(context,exception);
            };
        } else {
            if (lastHandler == null) {
                this.exceptionHandler = exceptionHandler;
            } else {
                // 否则表示存在上一个handler 已经增加了 ...
                ProxyDataFlowHandler<Context<T, R>> lastHandler = (ProxyDataFlowHandler<Context<T, R>>) this.lastHandler;
                lastHandler.addExceptionHandler(exceptionHandler);
                // 清除掉
                this.exceptionHandler = null;
            }
        }

        return this;
    }

    @Override
    public boolean isLazy() {
        return this.lazyFlag;
    }

    @Override
    public InternalContext<T, R> start() {
        List<DataFlowHandler<Context<T, R>>> handlerList = this.handlerList;
        this.lastHandler = null;
        // 废弃之前的所有处理器
        this.handlerList = new LinkedList<>();
        if (lazyFlag) {
            for (DataFlowHandler<Context<T, R>> tDataFlowHandler : handlerList) {
                // 直接绑定到了目标handler ...
                if (!doHandler(tDataFlowHandler,null)) {
                    break;
                }
            }
            // 执行完毕
            this.isPending = false;
        } else {
            // 如果一直没有异常处理器 ....
            for (DataFlowHandler<Context<T, R>> contextDataFlowHandler : handlerList) {
                // 直接绑定到了目标handler ...
                if (!doHandler(contextDataFlowHandler,null)) {
                    break;
                }
            }
        }

        // 统一处理异常信息
        if (exception != null) {
            CatchAllExceptionHandler<Context<T,R>> catchAllExceptionHandler = this.catchAllExceptionHandler;
            if (catchAllExceptionHandler != null) {
                catchAllExceptionHandler.handleException(this,exception);
                this.catchAllExceptionHandler = null;
                // 异常置空
                exception = null;
            } else {
                // 表示没有异常处理器
                throw new RuntimeException(exception);
            }
        }

        return this;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "[ flowIn: " + (isPending ? "pending" : dataFlow) + ", flowOut: " + result + "]";
    }
}

/**
 * 装饰目标处理器 ..
 *
 * @param <T> 上下文类型
 */
class ProxyDataFlowHandler<T> implements Context.DataFlowHandler<T> {

    public ProxyDataFlowHandler(Context.DataFlowHandler<T> handler,T context) {
        this.target = handler;
        this.context = context;
        this.exceptionHandler = new LinkedList<>();
    }

    private final Context.DataFlowHandler<T> target;

    private final T context;

    private final List<Context.CaughtDataFlowExceptionHandler<T>> exceptionHandler;

    @Override
    public void doHandle(T context) {
        try {
            target.doHandle(context);
        } catch (Exception e) {
            if (exceptionHandler.size() > 0) {
                for (Context.CaughtDataFlowExceptionHandler<T> caughtExceptionHandler : exceptionHandler) {
                    caughtExceptionHandler.handleException(context,e);
                }

                // 清理掉异常处理器 ..
                exceptionHandler.clear();
            } else {
                throw e;
            }
        }
    }

    public void addExceptionHandler(Context.CaughtDataFlowExceptionHandler<T> exceptionHandler) {
        this.exceptionHandler.add(exceptionHandler);
    }
}