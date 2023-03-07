package com.jianyue.lightning.boot.starter.util.dataflow.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author FLJ
 * @date 2022/10/26
 * @time 11:09
 * @Description 元组
 */
public class Tuple<T,S> {

    private final T t;
    private final S s;

    @JsonCreator
    public Tuple(@JsonProperty("t") T t, @JsonProperty("s") S s) {
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
