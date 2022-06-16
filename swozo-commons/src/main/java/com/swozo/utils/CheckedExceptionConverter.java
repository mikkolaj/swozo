package com.swozo.utils;

import com.swozo.function.ThrowingConsumer;
import com.swozo.function.ThrowingFunction;
import com.swozo.function.ThrowingRunnable;

import java.util.function.Consumer;
import java.util.function.Function;

public class CheckedExceptionConverter {
    private CheckedExceptionConverter() {
    }

    public static Runnable from(ThrowingRunnable throwingRunnable) {
        return () -> {
            try {
                throwingRunnable.run();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static<T> Consumer<T> from(ThrowingConsumer<T> throwingConsumer) {
        return (T t) -> {
            try {
                throwingConsumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static<T, R> Function<T, R> from(ThrowingFunction<T, R> throwingFunction) {
        return (T t) -> {
            try {
                return throwingFunction.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
