package com.swozo.utils;

import com.swozo.exceptions.PropagatingException;
import com.swozo.function.ThrowingRunnable;

import java.util.concurrent.Callable;

public class RetryHandler {
    private RetryHandler() {
    }

    public static <V> V retryExponentially(Callable<V> operation, int attempts) throws Exception {
        if (attempts < 1) {
            throw new IllegalArgumentException("Can't retry less than 1 time.");
        }

        var failCounter = 0;
        var backoffPeriod = 1000;
        Exception lastException = null;

        while (failCounter < attempts) {
            try {
                return operation.call();
            } catch (InterruptedException e) {
                handleInterrupt(e);
            } catch (Exception e) {
                lastException = e;
            }
            try {
                failCounter += 1;
                backoffPeriod *= 2;
                Thread.sleep(backoffPeriod);
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
