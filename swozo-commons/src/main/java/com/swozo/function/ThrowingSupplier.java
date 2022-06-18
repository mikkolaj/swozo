package com.swozo.function;

@FunctionalInterface
public interface ThrowingSupplier<T> {
    T get() throws Exception;
}
