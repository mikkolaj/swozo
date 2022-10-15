package com.swozo.utils;

public class RetryManager {
    private int failCounter;
    private long backoffPeriod;
    private Exception lastException;
    private final int attempts;

    public RetryManager(int attempts, long backoffPeriod) {
        this.failCounter = 0;
        this.lastException = null;
        this.backoffPeriod = backoffPeriod;
        this.attempts = attempts;
    }

    public RetryManager(int attempts) {
        this(attempts, 1000);
    }

    public boolean canContinue() {
        return failCounter < attempts;
    }

    public long nextBackoffMillis() {
        var curPeriod = backoffPeriod;
        failCounter++;
        backoffPeriod *= 2;
        return curPeriod;
    }

    public void setLastException(Exception lastException) {
        this.lastException = lastException;
    }

    public Exception getLastException() {
        assert lastException != null;
        return lastException;
    }
}
