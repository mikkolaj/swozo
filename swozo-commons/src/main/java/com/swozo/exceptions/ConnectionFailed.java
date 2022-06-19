package com.swozo.exceptions;

public class ConnectionFailed extends RuntimeException {
    public ConnectionFailed(String message) {
        super(message);
    }

    public ConnectionFailed(Throwable cause) {
        super(cause);
    }
}
