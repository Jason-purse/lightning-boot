package com.jianyue.lightning.boot.starter.util.dataflow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Tuple4 <T,S,R,V>{
    private final T first;

    private final S second;

    private final R three;

    private final V four;

    @JsonCreator
    public  Tuple4(@JsonProperty("first") T first, @JsonProperty("second") S second, @JsonProperty("three") R three,
                   @JsonProperty("four") V four) {
        this.first = first;
        this.second = second;
        this.three = three;
        this.four = four;
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

    public V getFour() {
        return four;
    }
}
