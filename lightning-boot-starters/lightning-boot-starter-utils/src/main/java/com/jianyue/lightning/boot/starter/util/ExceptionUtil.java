package com.jianyue.lightning.boot.starter.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author FLJ
 * @dateTime 2022/1/17 12:49
 * @description 异常信息工具类
 */
public class ExceptionUtil {

    public static <T extends RuntimeException, S> Supplier<S> throwAsSupplier(Supplier<T> throwException) {
        return () -> {
            throw throwException.get();
        };
    }

    /**
     * 产生一个运行时异常..
     *
     * @param throwException 异常
     * @param <T>            异常类型.
     * @return 异常生成器
     */
    public static <T extends RuntimeException, S> Supplier<S> throwAsSupplier(T throwException) {
        return () -> {
            throw throwException;
        };
    }

    /**
     * 产生一个运行时异常..
     *
     * @param <T>     异常类型.
     * @param message 异常信息
     * @return 异常生成器
     */
    @SuppressWarnings("unchecked")
    public static <T extends RuntimeException, S> Supplier<S> throwAsSupplier(String message) {
        return () -> {
            throw (T) new RuntimeException(message);
        };
    }





    public static <T extends RuntimeException, S> NOArgConsumer throwAs(Supplier<T> throwException) {
        return () -> {
            throw throwException.get();
        };
    }

    /**
     * 产生一个运行时异常..
     *
     * @param throwException 异常
     * @param <T>            异常类型.
     * @return 异常生成器
     */
    public static <T extends RuntimeException, S> NOArgConsumer throwAs(T throwException) {
        return () -> {
            throw throwException;
        };
    }

    /**
     * 产生一个运行时异常..
     *
     * @param <T>     异常类型.
     * @param message 异常信息
     * @return 异常生成器
     */
    @SuppressWarnings("unchecked")
    public static <T extends RuntimeException, S> NOArgConsumer throwAs(String message) {
        return () -> {
            throw (T) new RuntimeException(message);
        };
    }




    public static <T extends RuntimeException, S> Consumer<S> throwAsConsumer(Supplier<T> throwException) {
        return (S) -> {
            throw throwException.get();
        };
    }

    /**
     * 产生一个运行时异常..
     *
     * @param throwException 异常
     * @param <T>            异常类型.
     * @return 异常生成器
     */
    public static <T extends RuntimeException, S> Consumer<S> throwAsConsumer(T throwException) {
        return (S) -> {
            throw throwException;
        };
    }

    /**
     * 产生一个运行时异常..
     *
     * @param <T>     异常类型.
     * @param message 异常信息
     * @return 异常生成器
     */
    @SuppressWarnings("unchecked")
    public static <T extends RuntimeException, S> Consumer<S> throwAsConsumer(String message) {
        return (S) -> {
            throw (T) new RuntimeException(message);
        };
    }


}
