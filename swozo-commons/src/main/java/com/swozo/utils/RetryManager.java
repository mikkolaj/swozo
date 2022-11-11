package com.swozo.utils;

import java.util.Optional;

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

    public long registerAttempt() {
        var curPeriod = backoffPeriod;
        failCounter++;
        backoffPeriod *= 2;
        return curPeriod;
    }

    public Optional<Long> backoffMillis() {
        if (backoffPeriod > 0) {
            return Optional.of(backoffPeriod);
        } else {
            return Optional.empty();
        }
    }

    public void setLastException(Exception lastException) {
        this.lastException = lastException;
    }

    public Exception getLastException() {
        assert lastException != null;
        return lastException;
    }
}
