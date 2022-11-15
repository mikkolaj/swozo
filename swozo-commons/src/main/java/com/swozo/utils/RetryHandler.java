package com.swozo.utils;

import com.swozo.exceptions.PropagatingException;
import com.swozo.function.ThrowingRunnable;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RetryHandler {
    private RetryHandler() {
    }

    public static <V> V retryExponentially(Callable<V> operation, int attempts) throws Exception {
        if (attempts < 1) {
            throw new IllegalArgumentException("Can't attempt less than 1 time.");
        }

        var retryMgr = new RetryManager(attempts);

        while (retryMgr.canContinue()) {
            try {
                retryMgr.registerAttempt();
                return operation.call();
            } catch (InterruptedException e) {
                handleInterrupt(e);
            } catch (Exception e) {
                retryMgr.setLastException(e);
            }
            sleep(retryMgr.backoffMillis());
        }

        throw retryMgr.getLastException();
    }

    public static <T> CompletableFuture<T> withImmediateRetries(
            Supplier<CompletableFuture<T>> futureSupplier,
            int retries
    ) {
        var retryMgr = new RetryManager(retries, 0);
        return futureSupplier.get().exceptionallyComposeAsync(err -> handleRetries(futureSupplier, retryMgr, err));
    }

    public static <T> CompletableFuture<T> withExponentialBackoff(
            Supplier<CompletableFuture<T>> futureSupplier,
            int backoffRetries
    ) {
        var retryMgr = new RetryManager(backoffRetries);
        return futureSupplier.get().exceptionallyComposeAsync(err -> handleRetries(futureSupplier, retryMgr, err));
    }

    private static <T> CompletableFuture<T> handleRetries(
            Supplier<CompletableFuture<T>> requestSender,
            RetryManager retryMgr,
            Throwable previousErr
    ) {
        if (retryMgr.canContinue()) {
            retryMgr.registerAttempt();
            return requestSender.get().exceptionallyComposeAsync(err -> {
                sleep(retryMgr.backoffMillis());
                return handleRetries(requestSender, retryMgr, err);
            });
        }
        throw new TooManyFailuresException(previousErr.getCause());
    }

    private static void sleep(Optional<Long> possibleMillis) {
        possibleMillis.ifPresent(millis -> CheckedExceptionConverter.from(
                () -> Thread.sleep(millis)
        ).run());
    }

    public static void retryExponentially(ThrowingRunnable operation, int times) throws Exception {
        retryExponentially(VoidMapper.toCallableVoid(operation), times);
    }

    private static void handleInterrupt(InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new PropagatingException(e);
    }
}
