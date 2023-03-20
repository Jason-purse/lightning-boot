package com.jianyue.lightning.boot.starter.util;


import com.jianyue.lightning.boot.starter.util.dataflow.Context;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @AllArgsConstructor
    @NoArgsConstructor
    static class MyData {

        private String provinceCode;

        private String cityCode;

        private String distinctCode;
    }

    @Test
    public void complexOptionalFluxWithContext() {
//        MyData myData = new MyData();
//        printfConditionResult(myData);
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        printfConditionResult(myData);
//
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        myData.cityCode = "22";
//        printfConditionResult(myData);
//
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        myData.cityCode = "22";
//        myData.distinctCode = "33";
//        printfConditionResult(myData);
//
//
//        myData = new MyData();
//        myData.provinceCode = "1111";
//        printfConditionResult(myData);
//
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        myData.cityCode = "2222";
//        printfConditionResult(myData);
//
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        myData.cityCode = "22";
//        myData.distinctCode = "333333";
//        printfConditionResult(myData);
//
//
//        System.out.println(" ----------------- 两两组合 ---------------------");
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        myData.cityCode = "22";
//        printfConditionResult(myData);
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        myData.distinctCode = "33";
//        printfConditionResult(myData);
//
//
//        myData = new MyData();
//        myData.cityCode = "22";
//        myData.distinctCode = "33";
//        printfConditionResult(myData);
//
//
//        System.out.println(" ---------------- 单组合 ---------------------------");
//        myData = new MyData();
//        printfConditionResult(myData);
//
//        myData = new MyData();
//        myData.provinceCode = "11";
//        printfConditionResult(myData);
//
//        myData = new MyData();
//        myData.cityCode = "22";
//        printfConditionResult(myData);
//
//
//        myData = new MyData();
//        myData.distinctCode = "33";
//        printfConditionResult(myData);

        System.out.println("----------------- 部分条件数据  ---------------------------------");
        List<String> provinces = Arrays.asList("", "11", "111");
        List<String> cities = Arrays.asList("", "22", "2222");
        List<String> distincts = Arrays.asList("", "33", "33333");

        /**
         * 代码正确 ...
         */
        for (int i = 0; i < 3; i++) {
            for (int i1 = 0; i1 < 3; i1++) {
                for (int i2 = 0; i2 < 3; i2++) {
                    System.out.println("province: " + provinces.get(i) + " city: " + cities.get(i1) + " distinct: " + distincts.get(i2));
                    printfConditionResult(new MyData(provinces.get(i), cities.get(i1), distincts.get(i2)));
                }
            }
        }

    }

    private static void printfConditionResult(MyData myData) {
        String result = Context.<MyData, String>of(myData)
                .addDataFlowHandler(ctx -> {

                    OptionalFlux.stringOrNull(ctx.getDataFlow().provinceCode)
                            .consumeNull(() -> {
                                ctx.setResult("Root");
                            })
                            .flattenMap(code -> {

                                // 要么返回 null / 或者有 值
                                return OptionalFlux.stringOrNull(ctx.getDataFlow().provinceCode)
                                        .flattenMap(ele -> {
                                            return OptionalFlux.string(ele)
                                                    .switchMapIfTrueOrNull(value -> value.length() > 2, value -> {
                                                        return "real: " + value;
                                                    })
                                                    .orElse(() -> {
                                                        return "create: province";
                                                    });
                                        });
                            })
                            .consume(ctx::setResult)
                            // 有值的情况下
                            .flattenMap(province -> {

                                // 要么null 或者 有值 ..
                                return OptionalFlux.stringOrNull(ctx.getDataFlow().cityCode)
                                        .flattenMap(cityCode -> {
                                            return OptionalFlux.string(cityCode)
                                                    .switchMapIfTrueOrNull(value -> value.length() > 3, value -> {
                                                        return "real: " + value;
                                                    })
                                                    .orElse(() -> {
                                                        return "create: city";
                                                    });
                                        });
                            })
                            .consume(ctx::setResult)
                            .flattenMap(city -> {
                                return OptionalFlux.stringOrNull(ctx.getDataFlow().distinctCode)
                                        .flattenMap(distinctCode -> {
                                            return OptionalFlux.string(distinctCode)
                                                    .switchMapIfTrueOrNull(value -> value.length() > 4, value -> {
                                                        return "real: " + value;
                                                    }).orElse(() -> {
                                                        return "create: distinct";
                                                    });
                                        });

                            })
                            .consume(ctx::setResult);

                })
                .start()
                .getResult();

        System.out.println(result);
        System.out.println(" ------------------- ");
    }
}
