package com.jianyue.lightning.boot.starter.generic.crud.service;

public class JavaMainMethodForClassTests {
    public static void main(String[] args) {
        System.getProperties().forEach((key,value) -> System.out.println(key + "->" + value));
    }

    static class Value {
        static class Value2 {

            static class Value3 {
                public static void main(String[] args) {
                    System.out.println("value3");
                    System.getProperties().forEach((key,value) -> System.out.println(key + "->" + value));
                }
            }
        }
    }
}
