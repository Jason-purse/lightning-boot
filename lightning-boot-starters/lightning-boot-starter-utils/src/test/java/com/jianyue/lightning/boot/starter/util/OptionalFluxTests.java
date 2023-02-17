package com.jianyue.lightning.boot.starter.util;


import static com.jianyue.lightning.boot.starter.util.OptionalFlow.switchMapFunc;

public class OptionalFluxTests {
    public static void main(String[] args) {
        System.out.println(
                OptionalFlow
                        .of(123)
                        .map(ele -> ele * 23)
                        .consume(System.out::println)
                        .map(ele -> null)
                        .consumeOrElse(System.out::println, () -> {
                            System.out.println("参数为空");
                        })
                        .consume((ele) -> {
                            System.out.println("仅仅不为空,才消费");
                        })
                        .orElse(456)
                        .get()
        );


        OptionalFlow
                .of(123)
                .map(
                        // if-else
                        switchMapFunc(
                                ele -> ele > 123,
                                ele -> ele * 2,
                                () -> {
                                    System.out.println("没有数据");
                                    return 456;
                                }
                        )
                )
                .consume(System.out::println)
                .get();


        OptionalFlow
                .empty()
                .orElse(123)
                .consume(System.out::println)
                .get();
    }
}
