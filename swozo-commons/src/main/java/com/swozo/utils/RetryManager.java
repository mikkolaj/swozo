package com.swozo.utils;

public class RetryManager {
    private int failCounter = 0;
    private int backoffPeriod = 1000;
    private Exception lastException = null;
    private final int attempts;

    public RetryManager(int attempts) {
        if (attempts < 1) {
            throw new IllegalArgumentException("Can't retry less than 1 time.");
        }

        this.attempts = attempts;
    }

    public boolean canRetry() {
        return failCounter < attempts;
    }

    public long nextBackoffMillis() {
        failCounter++;
        backoffPeriod *= 2;
        return backoffPeriod;
    }

    public void setLastException(Exception lastException) {
        this.lastException = lastException;
    }

    public Exception getLastException() {
        assert lastException != null;
        return lastException;
    }
}
