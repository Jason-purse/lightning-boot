package com.jianyue.lightning.boot.starter.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;

/**
 * @author JASONJ
 * @dateTime: 2021-08-29 11:03:55
 * @description: optional flux
 */

public class OptionalUtil {

    /**
     * 无参消费者
     */
    @FunctionalInterface
    public interface NOArgConsumer {
        void accept();
    }

    /**
     * 上下文接口
     */
    public interface Context {


        @NotNull
        public static <T> DefaultContext<T> of(@NotNull T obj) {
            return new DefaultContext<>(obj);
        }

        /**
         * 默认 上下文实现
         *
         * @param <T> T 泛型类型
         */
        public static class DefaultContext<T> implements Context {

            @Nullable
            private final T obj;

            private DefaultContext(@Nullable T obj) {
                this.obj = obj;
            }


            public @Nullable T getObj() {
                return obj;
            }


            @Override
            public String getDataGraph() {
                return String.format("%s value: \n [%s]", Context.super.getDataGraph(), obj);
            }
        }

        /**
         * 空的 上下文
         */
        public static final class EmptyContext implements Context {

            public static final EmptyContext INSTANCE = new EmptyContext();

            private EmptyContext() {

            }

            @Override
            public String getDataGraph() {
                return String.format("%s value: [Empty]", Context.super.getDataGraph());
            }
        }

        /**
         * list Context 将多个上下文组装在一起
         */
        public static class ListContext implements Context {

            private final List<Context> contexts;

            private ListContext(@NotNull Context... contexts) {
                this(Arrays.asList(contexts));
            }

            private ListContext(List<Context> contexts) {
                this.contexts = Collections.unmodifiableList(contexts);
            }

            public List<Context> getContexts() {
                return contexts;
            }
        }

        public static class TupleContext implements Context {
            private final Context first;

            private final Context second;

            private TupleContext(Context first, Context second) {
                this.first = first;
                this.second = second;
            }

            public Context getFirst() {
                return first;
            }

            public Context getSecond() {
                return second;
            }
        }

        public static class TripleContext extends TupleContext {

            private final Context three;

            private TripleContext(
                    @NotNull Context first,
                    @NotNull Context second,
                    @NotNull Context three) {
                super(first, second);
                this.three = three;
            }

            @NotNull
            public Context getThree() {
                return three;
            }
        }

        /**
         * 获取最原始的 Context 类型
         *
         * @throws ClassCastException 如果强转一个不属于它的类型,则抛出异常 ..
         */
        @NotNull
        public default <T> T getNativeContext(Class<T> clazz) {
            return clazz.cast(this);
        }


        /**
         * 通过设置DataGraph 来实现{@link OptionalFlux#withLog()} 的日志记录
         *
         * @return
         */
        public default String getDataGraph() {
            return this.toString();
        }


        public static <T extends Context, S extends Context> BiFunction<T, S, T> mergeWithFirst() {
            return (ctx1, ctx2) -> ctx1;
        }

        public static <T extends Context, S extends Context> BiFunction<T, S, S> mergeWithLatest() {
            return (ctx1, ctx2) -> ctx2;
        }


        public static <T extends Context, S extends Context> BiFunction<T, S, TupleContext> mergeWithTuple() {
            return TupleContext::new;
        }


        public static <T extends Context> BiFunction<T, T, T> mergeWithFirstNotNull(BiFunction<@Nullable T, @Nullable T, @Nullable T> mergeHandler) {
            return (ctx, ctx2) -> {
                if (ctx != null && ctx2 != null) {
                    return mergeHandler.apply(ctx, ctx2);
                }

                if (ctx != null) {
                    return ctx;
                }

                return ctx2;
            };
        }


        public static <T extends DefaultContext<?>> BiFunction<T, T, T> mergeWithDefaultCtxContentFirstNotNull(BiFunction<@Nullable T, @Nullable T, @Nullable T> mergeHandler) {
            return (ctx1, ctx2) -> {
                if (ctx1 != null && ctx2 != null && ctx1.getObj() != null && ctx2.getObj() != null) {
                    return mergeHandler.apply(ctx1, ctx2);
                }

                if (ctx1 != null) {
                    if (ctx1.getObj() != null) {
                        return ctx1;
                    }
                }

                if (ctx2 != null) {
                    if (ctx2.getObj() != null) {
                        return ctx2;
                    }
                }

                // 两个都是空 ..
                return ctx1;
            };
        }

    }

    @NotNull
    public static <S> Context.DefaultContext<S> createContext(@NotNull S s) {
        return Context.of(s);
    }


    public static <S> OptionalFlux<S, Context.EmptyContext> of(@Nullable S value) {
        return of(Optional.ofNullable(value));
    }


    // --------- 对于下面的有些关于EmptyContext 的静态方法,虽然,没有传入上下文,但是创造的EmptyContent 本身是一个非空值 ..

    // 但是下面前两个构造器是一个例外,因为它强烈要求创建一个上下文为空的EmptyContent,但是创建一个为"null"的通常没有什么意思,但是提供 ...
    // 因为有一些函数和这样的空上下文条件配合,例如 mapContext,useContext 操作符 ...

    /**
     * 静态方法 构造一个OptionalFlux
     *
     * @param value value optional
     * @param <S>   type
     * @return new OptionalFlux
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S> OptionalFlux<S, Context.EmptyContext> noCtxOf(@NotNull Optional<S> value) {
        return new OptionalFlux<>(value, Optional.empty());
    }

    /**
     * 静态方法 构造一个OptionalFlux
     *
     * @param value value
     * @param <S>   type
     * @return new OptionalFlux
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S> OptionalFlux<S, Context.EmptyContext> noCtxOf(@Nullable S value) {
        return new OptionalFlux<>(Optional.ofNullable(value), Optional.empty());
    }

    /**
     * 静态方法 构造一个OptionalFlux
     *
     * @param value value optional
     * @param <S>   type
     * @return new OptionalFlux
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S> OptionalFlux<S, Context.EmptyContext> of(@NotNull Optional<S> value) {
        return new OptionalFlux<>(value, Context.EmptyContext.INSTANCE);
    }

    // 下面的静态方法构造器,仅有通过 Optional<CTX> 传入的上下文才有可能真的为空 ..

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, CTX extends Context> OptionalFlux<S, CTX> of(@NotNull Optional<S> value, @NotNull Optional<CTX> context) {
        return new OptionalFlux<S, CTX>(value, context);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, CTX extends Context> OptionalFlux<S, CTX> of(@NotNull Optional<S> value, @NotNull CTX context) {
        return new OptionalFlux<S, CTX>(value, context);
    }

    public static <S, CTX extends Context> OptionalFlux<S, CTX> of(@Nullable S value, @NotNull CTX context) {
        return new OptionalFlux<S, CTX>(Optional.ofNullable(value), context);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <S, CTX extends Context> OptionalFlux<S, CTX> of(@Nullable S value, @NotNull Optional<CTX> context) {
        return new OptionalFlux<S, CTX>(Optional.ofNullable(value), context);
    }

    public static <S> OptionalFlux<S, Context.EmptyContext> empty() {
        return of(Optional.empty());
    }

    public static <S> OptionalFlux<S, Context.EmptyContext> noCtxEmpty() {
        return noCtxOf(Optional.empty());
    }

    public static OptionalFlux<String, Context.EmptyContext> stringEmpty() {
        return empty();
    }

    /**
     * string的快捷方式 ...
     */
    public static OptionalFlux<String, Context.EmptyContext> stringOrNull(@Nullable String str) {
        return new OptionalFlux<>(ElvisUtil.stringElvisOrNull(str));
    }

    public static OptionalFlux<String, Context.EmptyContext> string(@Nullable String str, @NotNull String defaultStr) {
        return new OptionalFlux<>(ElvisUtil.stringElvis(str, defaultStr));
    }

    public static OptionalFlux<String, Context.EmptyContext> string(@NotNull String str) {
        return stringOrNull(str);
    }

    public static <T> OptionalFlux<Collection<T>, Context.EmptyContext> collection(@Nullable Collection<T> collection) {
        return new OptionalFlux<>(ElvisUtil.collectionElvisOrNull(collection));
    }

    public static <T> OptionalFlux<Collection<T>, Context.EmptyContext> collectionOrNull(@Nullable Collection<T> collection, @NotNull Collection<T> defaultCollection) {
        return new OptionalFlux<>(ElvisUtil.collectionElvis(collection, defaultCollection));
    }

    public static <T> OptionalFlux<List<T>, Context.EmptyContext> list(@Nullable List<T> collection) {
        return new OptionalFlux<>(ElvisUtil.listElvisOrNull(collection));
    }

    public static <T> OptionalFlux<List<T>, Context.EmptyContext> listOrNull(@Nullable List<T> collection, @NotNull List<T> defaultCollection) {
        return new OptionalFlux<>(ElvisUtil.listElvis(collection, defaultCollection));
    }


    /**
     * 主要根据 {@link #value} 属性进行可空性控制,实现去掉if-else化,并且携带了上下文编程,实现轻易的流程状态管理,以及行为切换 ...
     * <p>
     * 并且让代码具有更好的可读性 ..
     *
     * @param <S>   Value 值类型
     * @param <CTX> 上下文类型
     */
    public static class OptionalFlux<S, CTX extends Context> {


        /**
         * hold value
         */
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private final Optional<S> value;


        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private final Optional<CTX> context;

        /**
         * @param value optional value
         */
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private OptionalFlux(@NotNull Optional<S> value, @NotNull CTX ctx) {
            this(value, Optional.of(ctx));
        }

        /**
         * @param value 值
         * @param ctx   设置为Optional 是为了给静态方法提供(null值) 保证不可空 ..
         */
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        private OptionalFlux(@NotNull Optional<S> value, @NotNull Optional<CTX> ctx) {
            this.value = value;
            this.context = ctx;
        }

        private OptionalFlux(@Nullable S value) {
            this(Optional.ofNullable(value), Optional.empty());
        }


        @NotNull
        public Optional<S> getValue() {
            return this.value;
        }


        @NotNull
        public Optional<CTX> getContext() {
            return context;
        }

        /**
         * 总是执行,应该使用with ...
         *
         * @param consumer 消费器
         */
        @Deprecated
        // 消费null / real value
        public OptionalFlux<S, CTX> consumeOrNull(@NotNull Consumer<S> consumer) {
            if (this.value.isPresent()) {
                consumer.accept(this.value.get());
            } else {
                consumer.accept(null);
            }
            return this;
        }

        /**
         * 无论是否有值,都会执行
         *
         * @param consumer consumer
         */
        public OptionalFlux<S, CTX> withConsumeValue(@NotNull Consumer<@Nullable S> consumer) {
            consumer.accept(this.value.orElse(null));
            return this;
        }


        /**
         * 无论是否有值,都会执行
         *
         * @param consumer consumer
         */
        public OptionalFlux<S, CTX> withConsume(@NotNull BiConsumer<@Nullable S, @Nullable CTX> consumer) {
            consumer.accept(this.value.orElse(null), this.context.orElse(null));
            return this;
        }

        /**
         * 无论是否有值,都会执行
         *
         * @param consumer consumer
         */
        public OptionalFlux<S, CTX> withConsumeCtx(@NotNull Consumer<@Nullable CTX> consumer) {
            consumer.accept(ElvisUtil.getOrNull(context));
            return this;
        }

        public OptionalFlux<S, CTX> consume(@NotNull Consumer<@NotNull S> consumer) {
            this.value.ifPresent(consumer);
            return this;
        }

        /**
         * 注意 函数受 value的可空性控制
         */
        public OptionalFlux<S, CTX> consume(@NotNull BiConsumer<@NotNull S, @Nullable CTX> consumer) {
            this.value.ifPresent(e -> {
                consumer.accept(e, context.orElse(null));
            });
            return this;
        }

        public OptionalFlux<S, CTX> consume(@NotNull NOArgConsumer consumer) {
            this.value.ifPresent(ele -> consumer.accept());
            return this;
        }


        /**
         * 等价于 consume
         */
        public OptionalFlux<S, CTX> then(@NotNull Consumer<S> consumer) {
            this.value.ifPresent(consumer);
            return this;
        }

        // 同上
        public OptionalFlux<S, CTX> then(@NotNull NOArgConsumer consumer) {
            this.value.ifPresent(ele -> consumer.accept());
            return this;
        }

        public OptionalFlux<S, CTX> withLog() {
            System.out.println(String.format("OptionalFlux withLog: value: %s, ctx: %s", this.value.orElse(null), context.map(Context::getDataGraph).orElse(null)));
            return this;
        }

        public <CTX1 extends Context> OptionalFlux<S, CTX1> withContext(@NotNull CTX1 context) {
            return new OptionalFlux<>(value, context);
        }


        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public <CTX1 extends Context> OptionalFlux<S, CTX1> withContext(@NotNull Optional<CTX1> context) {
            return new OptionalFlux<>(value, context);
        }

        public <CTX1 extends Context> OptionalFlux<S, CTX1> withContext(@NotNull Supplier<@NotNull CTX1> contextSupplier) {
            return of(value, Optional.of(contextSupplier.get()));
        }

        /**
         * 无法实现,提前将上一个OptionalFlux的上下文拿出来 .. 哪种方式需要懒惰执行的方式 ..
         * <p>
         * 此OptionalFlux 不是懒惰执行的方式 ...
         * <p>
         * 懒惰执行方式:
         * ctx -> {
         * return val -> {
         * return OptionalFlux ...
         * <p>
         * or
         * <p>
         * return newVal
         * <p>
         * or
         * <p>
         * return Optional<newVal>
         * }
         * }
         * <p>
         * 目前通过此函数进行上下文的 函数执行切换 ...
         *
         * @param ctxFunction 将上下文进行映射 ..
         * @return 一个具有新状态的 OptionalFlux
         */
        @SuppressWarnings("unchecked")
        public <CTX1 extends Context> OptionalFlux<S, CTX1> mapContext(Function<@NotNull CTX, @NotNull CTX1> ctxFunction) {
            // 减少对象产生 ..
            if (this.isPresent()) {
                // 映射上下文
                return of(this.value, this.context.map(ctxFunction));
            }

            return (OptionalFlux<S, CTX1>) this;
        }

        public OptionalFlux<S, CTX> useContext(Consumer<CTX> ctx) {
            this.context.ifPresent(ctx);
            return this;
        }


        public <T> OptionalFlux<T, CTX> cast(@NotNull Class<T> targetClass) {
            // 否则啥也不做
            return this.map(targetClass::cast);
        }

        public OptionalFlux<S, CTX> existsForThrowEx(RuntimeException ex) {
            if (this.isPresent()) {
                throw ex;
            }
            return this;
        }

        // --------------------------------------- combine value ------------------------------------------------

        /**
         * 合并OptionalFlux , 并且合并 上下文(将前一个OptionalFlux的上下文进行继承,ref)
         * <p>
         * 仅仅是合并现有OptionalFlux的值而已 .. 继承前者OptionalFlux的上下文
         *
         * @apiNote 受{@link #value} 影响,所以,不使用with 修饰此方法 ...(但是此方法具有一定的with.. 方法的特性)
         */
        public OptionalFlux<S, CTX> combine(@NotNull OptionalFlux<S, CTX> other, @NotNull BiFunction<S, S, S> handler) {
            if (other.isPresent() && this.isPresent()) {
                return of(handler.apply(getResult(), other.getResult()), context);
            }
            return (this.isPresent() ? this : other).withContext(context);
        }


        // --------------------------------------- combine  value with combine same ctx --------------------------

        /**
         * 使用给定的OptionalFlux,以及指定值合并方式以及 上下文合并方式 合并出一个新的OptionalFlux
         * <p>
         *
         * @param ctxCombiner 当两个上下文都存在时进行合并的算法(合并结果可以为空).
         *                    内置了{@link Context#mergeWithFirstNotNull(BiFunction)} 来实现当上下文没有同时出现时如何合并 ..
         *                    (上下文都会尝试合并,不受{@link #value}的影响
         * @apiNote 值的合并是可选的, 也就是 两者不为空的时候,同时合并,否则谁不为空,取谁 ...
         * 类似于 笛卡尔积
         * <p>
         * 并且,合并的上下文是相同类型,无需担心类型变化 ...(代码改动几乎没有,因为不涉及到其他类型的交互) ..
         * <p>
         * 这里的意思是"combine by : `with context`" 这样的话,表示值 受 {@link #value} 影响,但是context 不受影响,直接合并 ..
         */
        public OptionalFlux<S, CTX> combineByWithSameContext(@NotNull OptionalFlux<S, CTX> other, BiFunction<@Nullable S, @Nullable S, @Nullable S> handler, @NotNull BiFunction<@NotNull CTX, @NotNull CTX, @Nullable CTX> ctxCombiner) {
            Optional<CTX> ctx = Optional.ofNullable(
                    Context.mergeWithFirstNotNull(ctxCombiner)
                            .apply(ElvisUtil.getOrNull(context), ElvisUtil.getOrNull(other.context))
            );
            if (other.isPresent() && this.isPresent()) {
                return of(handler.apply(getResult(), other.getResult()), ctx);
            }

            OptionalFlux<S, CTX> flux = this.isPresent() ? this : other;
            return flux.withContext(ctx);
        }

        // --------------------------------------- with combine ------------------------------------------------

        /**
         * 同上,
         * 合并OptionalFlux , 并且合并 上下文(将前一个OptionalFlux的上下文进行继承,ref)
         * <p>
         * 仅仅是合并现有OptionalFlux的值而已 .. 继承前者OptionalFlux的上下文
         *
         * @apiNote 它开始合并不同类型的值(并返回一个OptionalFlux), 不受 {@link #value}的影响,所以使用with 修饰方法 ..
         */
        public <T, Y> OptionalFlux<Y, CTX> withCombine(@NotNull OptionalFlux<T, CTX> other, @NotNull BiFunction<@Nullable S, @Nullable T, @Nullable Y> handler) {
            return this.withCombineByWithContext(other, handler, Context.mergeWithFirst());
        }


        // ---------------------------------------with  combine same context -----------------------------------

        /**
         * 仅仅合并上下文
         * <p>
         * 同理,内置了{@link Context#mergeWithFirstNotNull(BiFunction)} 仅当两个上下文都存在的时候,才会调用ctxConbiner ..
         *
         * @param other       合并其他OptionalFlux的上下文
         * @param ctxCombiner 合并方式(允许合并结果为空)
         * @return 合并之后的OptionalFlux
         * <p>
         * "with xxx[ combine context]"  每次发生 上下文合并 ...
         */
        public OptionalFlux<S, CTX> withCombineSameContext(@NotNull OptionalFlux<S, CTX> other, @NotNull BiFunction<@NotNull CTX, @NotNull CTX, @Nullable CTX> ctxCombiner) {
            return this.withContext(
                    Optional.ofNullable(
                            Context.mergeWithFirstNotNull(ctxCombiner)
                                    .apply(ElvisUtil.getOrNull(this.context), ElvisUtil.getOrNull(other.context))
                    )
            );
        }


        // 直接和上下文进行合并
        public OptionalFlux<S, CTX> withCombineSameContext(@NotNull CTX otherCtx, @NotNull BiFunction<@NotNull CTX, @NotNull CTX, @Nullable CTX> ctxCombiner) {
            return this.withContext(Optional.ofNullable(Context.mergeWithFirstNotNull(ctxCombiner).apply(ElvisUtil.getOrNull(this.context), otherCtx)));
        }


        // -------------------------------------------- with combine different context ------------------------

        /**
         * 此函数完全自定义合并方式(它不在内置{@link Context#mergeWithFirstNotNull(BiFunction)} 来实现非空上下文的选择 ...
         *
         * @param otherCtx    other ctx
         * @param ctxCombiner 自定义合并方式
         * @return 返回合并上下文之后的 OptionalFlux ..
         * @apiNote 此函数 通过`by` 一词完全指定通过 @param ctxCombiner 进行上下文合并 ...
         * <p>
         * 仅有"combineContextBy 才是真正自定义 ctx 合并策略"
         */
        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public <CTX1 extends Context, CTX2 extends Context> OptionalFlux<S, CTX2> withCombineContextBy(@NotNull Optional<CTX1> otherCtx, @NotNull BiFunction<@Nullable CTX, @Nullable CTX1, @Nullable CTX2> ctxCombiner) {
            return this.withContext(Optional.ofNullable(ctxCombiner.apply(ElvisUtil.getOrNull(context), ElvisUtil.getOrNull(otherCtx))));
        }

        public <CTX1 extends Context, CTX2 extends Context> OptionalFlux<S, CTX2> withCombineContextBy(@Nullable CTX1 otherCtx, @NotNull BiFunction<@Nullable CTX, @Nullable CTX1, @Nullable CTX2> ctxCombiner) {
            return this.withContext(Optional.ofNullable(ctxCombiner.apply(ElvisUtil.getOrNull(context), otherCtx)));
        }


        // ------------------------------------- with combine value and context ----------------------------------

        /**
         * // 此此方法以外,其他的合并大多数都是合并相同上下文 ...
         * 毫无疑问, 这是最强大的函数,能够根据你的转换函数,形成一个确定的OptionalFlux ...
         */
        public <Y, T, CTX1 extends Context, CTX2 extends Context> OptionalFlux<T, CTX2> withCombineByWithContext(@NotNull OptionalFlux<Y, CTX1> other, @NotNull BiFunction<@Nullable S, @Nullable Y, @Nullable T> handler, @NotNull BiFunction<@Nullable CTX, @Nullable CTX1, @Nullable CTX2> ctxCombiner) {
            return of(handler.apply(getResult(), other.getResult()), Optional.ofNullable(ctxCombiner.apply(ElvisUtil.getOrNull(context), ElvisUtil.getOrNull(other.context))));
        }


        public boolean isPresent() {
            return this.value.isPresent();
        }


        /**
         * if - else 逻辑
         *
         * @param supplier 不存在的映射
         * @return 返回OptionalFlux
         */
        @NotNull
        public OptionalFlux<S, CTX> orElse(@NotNull Supplier<S> supplier) {
            if (!this.isPresent()) {
                return of(supplier.get()).withContext(this.context);
            }
            return this;
        }

        @NotNull
        public OptionalFlux<S, CTX> orElseFlatTo(@NotNull Supplier<Optional<S>> supplier) {
            if (!this.isPresent()) {
                return of(supplier.get(), context);
            }
            return this;
        }

        // 请注意,它已经不会使用给定(supplier) OptionalFlux的上下文
        // 这里使用supplier的目的是,防止你错误使用OptionalFlux,从而执行了一些不期望的代码 ..
        // 但是如果你提前执行了OptionalFlux ,并使用() -> optionalFlux,那么没有任何作用 ..
        // 你需要检查你的代码是否存在错误 .. 是否错误调用了OptionalFlux ..
        @NotNull
        public OptionalFlux<S, CTX> orElseFlattenTo(@NotNull Supplier<OptionalFlux<S, CTX>> supplier) {
            if (!this.isPresent()) {
                return supplier.get().withContext(this.context);
            }
            return this;
        }

        /**
         * 尝试使用基于函数的例如: {@link #orElse(Optional)},{@link #orElse(OptionalFlux)} ,{@link #orElse(Supplier)}等等函数替换 ..
         * <p>
         * 但是如果是直接提供常量值,那么没有任何影响 ... 可以使用(例如 orElse(Collections.emptyList())
         *
         * @param target 提供对象
         * @return 另一个OptionalFlux
         */
        @Deprecated
        public OptionalFlux<S, CTX> orElse(@NotNull S target) {
            if (!this.value.isPresent()) {
                return of(target, context);
            }
            return this;
        }


        @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
        public OptionalFlux<S, CTX> orElse(@NotNull Optional<S> target) {
            if (!this.value.isPresent()) {
                return of(target, context);
            }
            return this;
        }

        // 注意 OptionalFlux 并没有懒惰执行的概念,所以切记你传入的target (是否提前执行了不该执行的动作)
        public OptionalFlux<S, CTX> orElse(@NotNull OptionalFlux<S, CTX> target) {
            return !isPresent() ? target : this;
        }

        /**
         * 无参消费者
         * <p>
         * 改用{@link #consumeNull(NOArgConsumer)} 代替 ..
         *
         * @param consumer consumer
         */
        @Deprecated
        public void orElse(@NotNull NOArgConsumer consumer) {
            if (!this.isPresent()) {
                consumer.accept();
            }
        }

        @NotNull
        public OptionalFlux<S, CTX> consumeNull(@NotNull NOArgConsumer consumer) {
            this.orElse(consumer);
            return this;
        }

        /**
         * copy 方法
         */
        @NotNull
        public OptionalFlux<S, CTX> copy() {
            return of(value).withContext(context);
        }

        /**
         * map
         *
         * @param function map函数
         * @param <T>      type
         * @return result
         */
        @NotNull
        public <T> OptionalFlux<T, CTX> map(@NotNull Function<S, T> function) {
            return of(this.value.map(function), this.context);
        }

        @NotNull
        public <T> OptionalFlux<T, CTX> flatMap(@NotNull Function<S, Optional<T>> function) {
            return of(this.value.flatMap(function), this.context);
        }


        /**
         * 此函数和flatt map并没有什么两样,但是只是在具有OptionalFlux的时候,直接从中获取值 得以方便 ..
         * 但是它不会合并上下文 ..
         * 并且继承上游上下文 ..
         *
         * @param function 用来映射上游的一个值到下游的OptionalFlux ..
         * @apiNote 保持是否执行函数之后, 上下文保持一致 .. 其他类似的函数有{@link #flatMap(Function)},{@link #map(Function)},
         * {@link #switchMap(Function, Supplier)},{@link #switchMapIfTrueOrNull(Predicate, Function)},{@link #switchMapIfFalseOrNull(Predicate, Function)},
         * {@link #orElse(Supplier)} 等等函数( 有关上下文切换,通过with开头的函数 以及相关合并函数进行控制 ...
         * <p>
         * 例如{@link #combineByWithSameContext(OptionalFlux, BiFunction, BiFunction)} (OptionalFlux, BiFunction, BiFunction)},{@link #withCombineSameContext(Context, BiFunction)} (Context, BiFunction)} 等相关函数来控制上下文合并 .
         */
        @NotNull
        public <T> OptionalFlux<T, CTX> flattenMap(@NotNull Function<S, @NotNull OptionalFlux<T, CTX>> function) {
            return this.value
                    .map(function)
                    .map(e -> e.withContext(context))
                    .orElseGet(() -> withValue((T) null));
        }

        // 但是,如果value 为空,flattenMap 可能无法执行,这里withContext 没有任何其他含义,仅仅是提供更多的参数,能够去产生下一个值 ...
        // 不涉及 上下文的合并 ..
        @NotNull
        public <T> OptionalFlux<T, CTX> flattenMapWithContext(
                @NotNull BiFunction<S, @Nullable CTX,
                        @NotNull OptionalFlux<T, CTX>> mapWithContextHandler) {

            return this
                    .flattenMap(e -> {
                        return mapWithContextHandler.apply(e, ElvisUtil.getOrNull(context));
                    });
        }


        @NotNull
        public <T> OptionalFlux<T, CTX> flatMapWithContext(BiFunction<S, @Nullable CTX, @NotNull Optional<T>> mapWithContextHandler) {
            return this.flatMap(e -> {
                return mapWithContextHandler.apply(e, ElvisUtil.getOrNull(context));
            });
        }

        @NotNull
        public <T> OptionalFlux<T, CTX> mapWithContext(BiFunction<S, @Nullable CTX, T> mapWithContextHandler) {
            return this.map(e -> {
                return mapWithContextHandler.apply(e, ElvisUtil.getOrNull(context));
            });
        }


        /**
         * 直接替换 !!!
         */
        @NotNull
        public <T> OptionalFlux<T, CTX> withValue(@NotNull Supplier<T> supplier) {
            return of(supplier.get()).withContext(this.context);
        }

        /**
         * 直接替换 !!!
         */
        @NotNull
        public <T> OptionalFlux<T, CTX> withValueSupplier(@NotNull Supplier<Optional<T>> supplier) {
            return of(supplier.get()).withContext(this.context);
        }


        /**
         * 直接替换 !!!
         */
        public <T> OptionalFlux<T, CTX> withValue(@Nullable T target) {
            return of(target, this.context);
        }

        /**
         * 如果存在,否则 empty
         * 有时候不关心 上一个值是什么,只想它成立的情况下,跳入下一个目标 ..
         * <p>
         * 不建议使用,方法名称存在模糊性(无法明确知道它是可选执行,还是强制执行)
         *
         * @param target 目标对象 ..
         * @param <T>    目标类型
         * @return empty or optionalFlux<T>
         */
        @Deprecated
        public <T> OptionalFlux<T, CTX> to(Supplier<T> target) {
            return this.map(ele -> target.get());
        }

        /**
         * 同上(但是这个方法是可选执行)
         */
        @Deprecated
        public <T> OptionalFlux<T, CTX> flattenTo(Supplier<OptionalFlux<T, CTX>> target) {
            return this.flattenMap(ele -> target.get());
        }

        /**
         * 同上(但是这个方法是可选执行)
         */
        @Deprecated
        public <T> OptionalFlux<T, CTX> flatTo(Supplier<Optional<T>> target) {
            return this.flatMap(ele -> target.get());
        }

        /**
         * // switch map (三元表达式 推断)
         * <p>
         * 如果存在,则,否则,则 ...
         *
         * @param function if true exec
         * @param supplier if false exec
         * @param <T>      t type
         * @return new OptionalFlux
         */
        public <T> OptionalFlux<T, CTX> switchMap(@NotNull Function<S, T> function, @NotNull Supplier<T> supplier) {
            // 这样做的目的是,防止 supplier 替换了function的结果
            // 之前的函数是 this.map(function).orElse(supplier) 这很有可能导致 supplier 替换了function的结果 ..
            if (isPresent()) {
                return map(function);
            }
            // 这个时候可以调用 ..
            return withValue(supplier.get());
        }


        // 针对表达式进行 if-else 判断 ..
        @Deprecated
        public <T> OptionalFlux<T, CTX> ifTrueForSwitchMap(@NotNull Predicate<S> predicate, @NotNull Function<S, T> trFunction) {
            // 直接map ... 即可 ..
            return this.map(SwitchUtil.switchMapFuncForTrue(predicate, trFunction));
        }

        @Deprecated
        public <T> OptionalFlux<T, CTX> ifFalseForSwitchMap(@NotNull Predicate<S> predicate, @NotNull Function<S, T> frFunction) {
            return this.map(SwitchUtil.switchMapFuncForFalse(predicate, frFunction));
        }


        public <T> OptionalFlux<T, CTX> switchMapIfTrueOrNull(@NotNull Predicate<S> predicate, @NotNull Function<S, T> trFunction) {
            // 直接map ... 即可 ..
            return this.map(SwitchUtil.switchMapFuncForTrue(predicate, trFunction));
        }

        public <T> OptionalFlux<T, CTX> switchMapIfFalseOrNull(@NotNull Predicate<S> predicate, @NotNull Function<S, T> frFunction) {
            return this.map(SwitchUtil.switchMapFuncForFalse(predicate, frFunction));
        }


        /**
         * 统一返回结果!
         *
         * @return null Or Object
         * <p>
         * if return null , may not call ifPresent  or OrElse or return result is null!
         */
        @Nullable
        public S getResult() {
            return this.value.orElse(null);
        }


        /**
         * @param clazz result class
         * @param <T>   T
         * @return result
         * @throws ClassCastException class can't converted to T
         */
        @Deprecated
        public <T> T getResult(Class<T> clazz) {
            return clazz.cast(getResult());
        }

        /**
         * @throws ClassCastException class can't converted to T
         */
        @SuppressWarnings("unchecked")
        @Deprecated
        public <T> List<T> getResultForList(Class<T> eleType) {
            return ((List<T>) getResult());
        }

        @Nullable
        public Class<?> getTargetClass() {
            return this.value.map(Object::getClass).orElse(null);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o == null) {
                return false;
            }
            if (this == o) {
                return true;
            }
            if (getClass() != o.getClass()) {
                return false;
            }
            OptionalFlux<?, ?> that = (OptionalFlux<?, ?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this, value);
        }
    }
}