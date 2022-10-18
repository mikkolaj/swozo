package com.swozo.api.common.files.exceptions;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;
import com.swozo.api.web.exceptions.types.common.ValidationNames;

import java.util.List;
import java.util.Map;

public class IllegalFilenameException extends ApiException {

    private IllegalFilenameException(String message, Map<String, Object> additionalData) {
        super(message, ErrorType.ILLEGAL_FILENAME, additionalData);
    }

    public static IllegalFilenameException of(String filename, List<String> forbiddenCharacters) {
        return new IllegalFilenameException(
                "Invalid filename: " + filename,
                Map.of(ValidationNames.Errors.FORBIDDEN_CHARACTERS, forbiddenCharacters)
            );
    }
}
