package com.swozo.api.web.exceptions.types.files;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class FileNotFoundException extends ApiException {
    public FileNotFoundException(String message) {
        super(message, ErrorType.FILE_NOT_FOUND);
    }

    public static FileNotFoundException inContext(String context, Long fileId) {
        return new FileNotFoundException("File: " + fileId + " doesn't exist in context: " + context);
    }

    public static FileNotFoundException globally(Long fileId) {
        return new FileNotFoundException("File: " + fileId + " doesn't exist");
    }
}
