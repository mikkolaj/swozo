package com.swozo.api.orchestrator.exceptions;

import java.net.http.HttpResponse;

public class InvalidStatusCodeException extends RuntimeException {
    public InvalidStatusCodeException(String message) {
        super(message);
    }

    public InvalidStatusCodeException(HttpResponse<?> response) {
        this(String.format(
                "Invalid status code from uri: %s, got code: %d with body: %s.",
                response.uri(),
                response.statusCode(),
                response.body()
        ));
    }
}
