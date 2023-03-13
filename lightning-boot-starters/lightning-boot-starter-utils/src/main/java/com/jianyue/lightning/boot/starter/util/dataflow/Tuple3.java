package com.jianyue.lightning.boot.starter.util.dataflow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author FLJ
 * @date 2023/3/13
 * @time 15:47
 * @Description 三元组 !!!
 */
public class Tuple3<T,S,R> {

    private final T first;

    private final S second;

    private final R three;


    @JsonCreator
    public  Tuple3(@JsonProperty("first") T first, @JsonProperty("second") S second, @JsonProperty("three") R three) {
        this.first = first;
        this.second = second;
        this.three = three;
    }

    public R getThree() {
        return three;
    }

    public S getSecond() {
        return second;
    }

    public T getFirst() {
        return first;
    }
}
