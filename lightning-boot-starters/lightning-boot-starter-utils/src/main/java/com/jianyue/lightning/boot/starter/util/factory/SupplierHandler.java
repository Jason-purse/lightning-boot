package com.jianyue.lightning.boot.starter.util.factory;

import java.util.function.Supplier;

public interface SupplierHandler<T> extends Handler {

    T get();

    static <T> SupplierHandler<T> of(Supplier<T> transformer) {
        return new DefaultSupplierHandler<>(transformer);
    }

    static <T> SupplierHandler<T> of(T transformer) {
        return new DefaultSupplierHandler<>(() -> transformer);
    }
}

class DefaultSupplierHandler<T> implements SupplierHandler<T> {
    private final Supplier<T> supplier;

    public DefaultSupplierHandler(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return supplier.get();
    }
}