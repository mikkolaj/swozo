package com.swozo.api.common.files.exceptions;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class DuplicateFileException extends ApiException {
    private DuplicateFileException(String message) {
        super(message, ErrorType.DUPLICATE_FILE);
    }

    public static DuplicateFileException withName(String name) {
        return new DuplicateFileException("File: " + name + " already exists in this context");
    }
}
