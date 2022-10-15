package com.swozo.utils;

import com.swozo.exceptions.PropagatingException;
import com.swozo.function.ThrowingRunnable;

import java.util.concurrent.Callable;

public class RetryHandler {
    private RetryHandler() {
    }

    public static <V> V retryExponentially(Callable<V> operation, int attempts) throws Exception {
        var retryMgr = new RetryManager(attempts);

        while (retryMgr.canRetry()) {
            try {
                return operation.call();
            } catch (InterruptedException e) {
                handleInterrupt(e);
            } catch (Exception e) {
                retryMgr.setLastException(e);
            }
            try {
                Thread.sleep(retryMgr.nextBackoffMillis());
            } catch (InterruptedException e) {
                handleInterrupt(e);
            }
        }

        throw retryMgr.getLastException();
    }

    public static void retryExponentially(ThrowingRunnable operation, int times) throws Exception {
        retryExponentially(VoidMapper.toCallableVoid(operation), times);
    }

    private static void handleInterrupt(InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new PropagatingException(e);
    }
}
