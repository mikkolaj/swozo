package com.swozo.exceptions;

public class PropagatingException extends RuntimeException {
    public PropagatingException(String message) {
        super(message);
    }

    public PropagatingException(Throwable cause) {
        super(cause);
    }
}
