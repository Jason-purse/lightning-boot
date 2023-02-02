package com.jianyue.lightning.boot.starter.util.dataflow.impl;
/**
 * @author FLJ
 * @date 2022/10/26
 * @time 11:09
 * @Description 元组
 */
public class Tuple<T,S> {

    private final T t;
    private final S s;

    public Tuple(T t,S s) {
        this.t = t;
        this.s = s;
    }


    public T getFirst() {
        return t;
    }

    public S getSecond() {
        return s;
    }
}
