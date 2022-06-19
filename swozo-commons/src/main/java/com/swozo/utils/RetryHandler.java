package com.swozo.utils;

import com.swozo.exceptions.PropagatingException;
import com.swozo.function.ThrowingRunnable;

import java.util.concurrent.Callable;

public class RetryHandler {
    private RetryHandler() {
    }

    public static <V> V retryExponentially(Callable<V> operation, int times) throws Exception {
        if (times < 1) {
            throw new IllegalArgumentException("Can't retry less than 1 time.");
        }

        var failCounter = 1;
        var backoff = 1000;
        Exception lastException = null;

        while (failCounter <= times) {
            try {
                return operation.call();
            } catch (InterruptedException e) {
                handleInterrupt(e);
            } catch (Exception e) {
                lastException = e;
            }
            try {
                failCounter += 1;
                backoff *= 2;
                Thread.sleep(backoff);
            } catch (InterruptedException e) {
                handleInterrupt(e);
            }
        }
        assert lastException != null;
        throw lastException;
    }

    public static void retryExponentially(ThrowingRunnable operation, int times) throws Exception {
        retryExponentially(VoidMapper.toCallableVoid(operation), times);
    }

    private static void handleInterrupt(InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new PropagatingException(e);
    }
}
