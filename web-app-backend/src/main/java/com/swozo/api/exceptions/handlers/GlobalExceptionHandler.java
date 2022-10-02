package com.swozo.api.exceptions.handlers;

import com.swozo.api.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

}
