package com.swozo.function;

import java.util.Objects;

@FunctionalInterface
public interface ThrowingBiConsumer<T, S> {
    void accept(T t, S s) throws Exception;

    default ThrowingBiConsumer<T, S> andThen(ThrowingBiConsumer<? super T, ? super S> after) {
        Objects.requireNonNull(after);
        return (T t, S s) -> {
            accept(t, s);
            after.accept(t, s);
        };
    }
}
