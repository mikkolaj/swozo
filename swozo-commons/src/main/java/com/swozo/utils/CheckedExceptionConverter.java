package com.swozo.utils;

import com.swozo.exceptions.PropagatingException;
import com.swozo.function.*;
import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CheckedExceptionConverter {
    private CheckedExceptionConverter() {
    }

    public static Runnable from(ThrowingRunnable throwingRunnable) {
        return () -> execute(
                VoidMapper.toCallableVoid(throwingRunnable),
                RuntimeException::new
        );
    }

    public static Runnable from(ThrowingRunnable throwingRunnable, Function<Throwable, RuntimeException> converter) {
        return () -> execute(
                VoidMapper.toCallableVoid(throwingRunnable),
                converter
        );
    }

    public static <T> Supplier<T> from(ThrowingSupplier<T> throwingSupplier) {
        return () -> execute(
                throwingSupplier::get,
                RuntimeException::new
        );
    }

    public static <T> Supplier<T> from(ThrowingSupplier<T> throwingSupplier, Function<Throwable, RuntimeException> converter) {
        return () -> execute(
                throwingSupplier::get,
                converter
        );
    }

    public static <T> Consumer<T> from(ThrowingConsumer<T> throwingConsumer) {
        return (T t) -> execute(
                VoidMapper.toCallableVoid(() -> throwingConsumer.accept(t)),
                RuntimeException::new
        );
    }

    public static <T> Consumer<T> from(ThrowingConsumer<T> throwingConsumer, Function<Throwable, RuntimeException> converter) {
        return (T t) -> execute(
                VoidMapper.toCallableVoid(() -> throwingConsumer.accept(t)),
                converter
        );
    }

    public static <T, S> BiConsumer<T, S> from(ThrowingBiConsumer<T, S> throwingConsumer) {
        return (T t, S s) -> execute(
                VoidMapper.toCallableVoid(() -> throwingConsumer.accept(t, s)),
                RuntimeException::new
        );
    }

    public static <T, S> BiConsumer<T, S> from(ThrowingBiConsumer<T, S> throwingConsumer, Function<Throwable, RuntimeException> converter) {
        return (T t, S s) -> execute(
                VoidMapper.toCallableVoid(() -> throwingConsumer.accept(t, s)),
                converter
        );
    }

    public static <T, R> Function<T, R> from(ThrowingFunction<T, R> throwingFunction) {
        return (T t) -> execute(
                () -> throwingFunction.apply(t),
                RuntimeException::new
        );
    }

    public static <T, R> Function<T, R> from(ThrowingFunction<T, R> throwingFunction, Function<Throwable, RuntimeException> converter) {
        return (T t) -> execute(
                () -> throwingFunction.apply(t),
                converter
        );
    }

    private static <T> T execute(Callable<T> task, Function<Throwable, RuntimeException> converter) {
        try {
            return task.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PropagatingException(e);
        } catch (Exception e) {
            throw converter.apply(e);
        }
    }

    @SneakyThrows
    public void sneakyThrow(Throwable ex) {
        throw ex;
    }

}
