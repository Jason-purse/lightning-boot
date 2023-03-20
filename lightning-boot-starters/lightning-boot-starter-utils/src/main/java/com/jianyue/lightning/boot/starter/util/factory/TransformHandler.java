package com.jianyue.lightning.boot.starter.util.factory;

import java.util.function.Function;

public interface TransformHandler<DATA, T> extends Handler {

        T get(DATA value) throws Exception;

        static <DATA, T> TransformHandler<DATA, T> of(Function<DATA, T> transformer) {
            return new DefaultTransformHandler<>(transformer);
        }

        static <DATA, T> TransformHandler<DATA, T> of(T transformer) {
            return new DefaultTransformHandler<>((ele) -> transformer);
        }

    }

class DefaultTransformHandler<DATA, T> implements TransformHandler<DATA, T> {
    private final Function<DATA, T> transformer;

    public DefaultTransformHandler(Function<DATA, T> transformer) {
        this.transformer = transformer;
    }


    @Override
    public T get(DATA value) {
        return transformer.apply(value);
    }
}