package com.swozo.utils;

public class TooManyFailuresException extends RuntimeException {
    public TooManyFailuresException(Throwable ex) {
        super(ex);
    }
}
