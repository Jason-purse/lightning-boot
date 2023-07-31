package com.jianyue.lightning.boot.starter.util;

import org.assertj.core.groups.Tuple;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author jasonj
 * @date 2023/7/30
 * @time 18:10
 * @description OptionalFlux的 单元测试用例 ..
 **/
public class OptionalUtilTests {

    /**
     * 上下文使用判断
     */
    @Test
    public void test() {

        OptionalUtil.empty()
                .withContext(OptionalUtil.createContext(new HashMap<String, String>()))
                .useContext(ctx -> {
                    System.out.println("ctx is map");

                    ctx.getObj().put("key1", "v1");
                    ctx.getObj().put("key2", "v1");
                    ctx.getObj().put("key3", "v1");
                    ctx.getObj().put("key4", "v1");

                    Assert.assertEquals(4, ctx.getObj().size());
                });


    }

    /**
     * 可空性判断
     */
    @Test
    public void orElseTest() {
        OptionalUtil.empty()
                .withContext(OptionalUtil.createContext(Tuple.tuple(1, 2, 3, 4)))
                .withConsumeValue(Assert::assertNull);
    }

    /**
     * map 函数
     */
    @Test
    public void mapTest() {
        OptionalUtil.empty()
                .withValue(3)
                .switchMapIfTrueOrNull(e -> e == 3, e -> e * 3)
                .withConsumeValue(Assert::assertNotNull)
                .map(String::valueOf)
                .withConsumeValue(e -> Assert.assertTrue(e instanceof String))
                .cast(String.class);


        // 为空,所有的可选执行操作符 都不会执行 ...
        OptionalUtil.empty()
                .map(e -> 3)
                .flatMap(e -> Optional.of(e * 3))
                .flattenMap(e -> OptionalUtil.of(123))
                .withLog()
                .withConsumeValue(Assert::assertNull);

        OptionalUtil.empty()
                .withContext(OptionalUtil.createContext("123"))
                .withValue(3)
                // 上下文 仅仅继承前者OptionalFlux的上下文 ...
                .flattenMap(e -> OptionalUtil.of("4455", OptionalUtil.createContext("345345")))
                .withLog()
                .withConsumeValue(e -> {
                    Assert.assertSame("4455", e);
                });

    }

    @Test
    public void flatMap() {
        OptionalUtil.empty()
                .withValue(1)
                .flatMap(e -> Optional.of(423))
                .withConsumeValue(e -> {
                    // 大于 125 无法缓存
                    Assert.assertTrue(e != null && e.equals(423));
                });
    }


    @Test
    public void flattenMap() {
        OptionalUtil.empty()
                .withValue(1)
                .flattenMap(e -> OptionalUtil.of(33))
                .withConsumeValue(e -> {
                    Assert.assertTrue(e != null && e == 33);
                });
    }

    /**
     * consume*,then / orElse 函数是否正常运行
     */
    @Test
    public void consume() {
        // 非空消费
        AtomicInteger count = new AtomicInteger(1);
        OptionalUtil.empty()
                .withValue(12)
                .consume(e -> {
                    count.addAndGet(-1);
                });

        Assert.assertEquals(0, count.get());


        OptionalUtil.empty()
                .withValue(23)
                .then(e -> {
                    count.decrementAndGet();
                });

        Assert.assertEquals(-1,count.get());

        OptionalUtil.empty()
                .withValue(456)
                .consume(count::decrementAndGet);

        Assert.assertEquals(-2,count.get());

        OptionalUtil.empty()
                .withValue(456)
                .withConsumeValue((e) -> {
                    count.decrementAndGet();
                });

        Assert.assertEquals(-3,count.get());



        OptionalUtil.empty()
                .withValue(456)
                .consumeNull(count::decrementAndGet);

        Assert.assertEquals(-3,count.get());


        OptionalUtil.empty()
                .withValue(456)
                .orElse(count::decrementAndGet);

        Assert.assertEquals(-3,count.get());



        // 空 断言



        OptionalUtil.empty()
                .withConsumeValue((e) -> {
                    count.decrementAndGet();
                });

        Assert.assertEquals(-4,count.get());

        OptionalUtil.empty()
                .consumeNull(count::decrementAndGet);

        Assert.assertEquals(-5,count.get());


        OptionalUtil.empty()
                .then(count::decrementAndGet);

        Assert.assertEquals(-5,count.get());

        OptionalUtil.empty()
                .then(e -> {
                    count.decrementAndGet();
                });

        Assert.assertEquals(-5,count.get());


        OptionalUtil.empty()
                .consume(e -> {
                    count.decrementAndGet();
                });

        Assert.assertEquals(-5,count.get());

        OptionalUtil.empty()
                .consume(count::decrementAndGet);

        Assert.assertEquals(-5,count.get());
    }


    /**
     * 上下文映射,使用,合并
     */
    @Test
    public void context() {
        OptionalUtil.empty().withValue(1)

                // 上下文不为空,才映射
                .mapContext(ctx -> {
                    return OptionalUtil.createContext("1231");
                })
                // 不管有无上下文,这个函数都将执行 ...
                .withConsumeCtx(Assert::assertNotNull);


        OptionalUtil.of(1,Optional.empty())
                .mapContext(ctx -> {
                    return OptionalUtil.createContext("1231");
                })
                .withConsumeCtx(Assert::assertNull);


        OptionalUtil.noCtxOf(123)
                .mapContext(ctx -> OptionalUtil.createContext("12312"))
                .withConsumeCtx(Assert::assertNull);


        // context 的可选使用
        AtomicInteger count = new AtomicInteger(1);
        OptionalUtil.empty()
                .useContext(ctx -> {
                    count.addAndGet(-1);
                });

        Assert.assertEquals(0,count.get());



        // 强制修改上下文

        OptionalUtil.empty()
                .withContext(Optional.of(OptionalUtil.createContext(1231)))
                .withConsumeCtx(ctx -> {
                    Assert.assertEquals(1231,ctx.getObj().longValue());
                });

        OptionalUtil.empty()
                .withContext((Supplier<OptionalUtil.Context.DefaultContext<Integer>>) () -> OptionalUtil.createContext(1231))
                .withConsumeCtx(ctx -> {
                    Assert.assertEquals(1231L,ctx.getObj().longValue());
                });


        OptionalUtil.empty()
                .withContext(OptionalUtil.createContext(1231))
                .withConsumeCtx(ctx -> {
                    Assert.assertEquals(1231,ctx.getObj().longValue());
                });


        // 上下文的 可选合并

        OptionalUtil.empty()
                .withContext(OptionalUtil.createContext("234"))
                // 可选合并(在合并值的时候,尝试合并值)
                .withValue(1)
                .combineByWithSameContext(OptionalUtil.of(2).withContext(OptionalUtil.createContext("234")),Integer::sum,(ctx,ctx1) -> {
                    return OptionalUtil.createContext(ctx.getObj() + "-" + ctx1.getObj());
                })
                .withConsume((value,ctx) -> {
                    Assert.assertNotNull(value);
                    Assert.assertTrue(ctx != null && ctx.getObj() != null);
                });

        OptionalUtil.noCtxEmpty()
                .withValue((Integer) null)
                .withContext(OptionalUtil.createContext("234"))
                // 可选合并(可选的合并值,但是都会执行 ...) - 受 value 影响 ..
                .combineByWithSameContext(OptionalUtil.of(2).withContext(OptionalUtil.createContext("234")),Integer::sum,(ctx,ctx1) -> {
                    return OptionalUtil.createContext(ctx.getObj() + "-" + ctx1.getObj());
                })
                // 进行消费 ..
                .withConsume((value,ctx) -> {
                    Assert.assertNotNull(value);
                    Assert.assertEquals(2,value.intValue());
                    Assert.assertFalse(ctx != null && Objects.equals(ctx.getObj(), "234"));
                    Assert.assertTrue(ctx != null && Objects.equals(ctx.getObj(),"234-234"));
                });


        // 强制合并
        OptionalUtil.noCtxEmpty()
                .withValue((Integer) null)
                .withContext(OptionalUtil.createContext(""))
                // 仅仅合并相同上下文
                .withCombineSameContext(
                        OptionalUtil.createContext("123"),
                        (ctx1,ctx2) -> {
                            return OptionalUtil.createContext(ctx1.getObj() + ctx2.getObj());
                        }
                )
                .withConsumeCtx(ctx -> {
                    Assert.assertTrue(ctx != null && ctx.getObj().equals("123"));
                });


        // 强制合并
        OptionalUtil.empty()
                // 合并完全取决于自定义方式 ...
                .withCombineContextBy(
                        OptionalUtil.createContext("123"),
                        OptionalUtil.Context.mergeWithTuple()
                )
                .withConsumeCtx( ctx -> {
                    Assert.assertTrue(ctx instanceof OptionalUtil.Context.TupleContext);
                    Assert.assertTrue(ctx.getFirst() == OptionalUtil.Context.EmptyContext.INSTANCE);
                    Assert.assertTrue(ctx.getFirst() != null);
                });
    }


    @Test
    public void resultAndContext() {
        Object result = OptionalUtil.empty()
                .withConsumeValue(e -> {
                    Assert.assertNull(e);
                })
                .getResult();

        Assert.assertNull(result);

        Optional<OptionalUtil.Context.EmptyContext> context = OptionalUtil.empty()
                .getContext();

        Assert.assertNotNull(context);
    }


    @Test
    public void strategyTest() {

        OptionalUtil.of(1)
                .switchMapIfTrueOrNull( e ->  e == 1,(e) -> {
                    return e * 3;
                })
                .orElse(() -> 10)
                .withLog()
                .consume(e -> {
                    System.out.println(e);
                });
    }

}
