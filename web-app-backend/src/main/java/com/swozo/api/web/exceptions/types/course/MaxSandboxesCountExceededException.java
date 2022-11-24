package com.swozo.api.web.exceptions.types.course;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class MaxSandboxesCountExceededException extends ApiException {
    public MaxSandboxesCountExceededException() {
        super("sandboxes count exceeded", ErrorType.SANDBOXES_COUNT_EXCEEDED);
    }
}
