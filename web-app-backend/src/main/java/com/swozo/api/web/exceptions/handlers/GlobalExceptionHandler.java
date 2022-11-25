package com.swozo.api.web.exceptions.handlers;

import com.swozo.api.web.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    private ResponseEntity<Object> handleApiException(ApiException apiException, WebRequest request) {
        logger.debug("Handling Api Error", apiException);
        return handleExceptionInternal(
                apiException,
                apiException.toSerializable(),
                new HttpHeaders(),
                apiException.getErrorType().getApiError().getStatusCode(),
                request
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    private ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e, WebRequest request) {
        logger.debug("Entity not found", e);
        return handleExceptionInternal(e, new ErrorMessage("Resource not found"), new HttpHeaders(), HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(Exception.class)
    private ResponseEntity<Object> handleFallbackException(Exception exception, WebRequest request) {
        logger.error("Handling fallback error", exception);
        return handleExceptionInternal(
                exception,
                new ErrorMessage("Invalid request"),
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                request
        );
    }

    private record ErrorMessage(String message) {}
}
