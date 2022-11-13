package com.swozo.api.web.exceptions.types.common;

import java.util.*;
import java.util.function.Supplier;

public class ValidationErrors {
    private final Map<String, ValidationError> errors;

    private ValidationErrors(Map<String, ValidationError> errors) {
        this.errors = errors;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void throwIfAnyPresent(String message) {
        if (!errors.isEmpty()) {
            throw new ValidationException(message, errors);
        }
    }

    public static class Builder {
        private final Map<String, ValidationError> errors;
        public static final String SEPARATOR = ".";

        private Builder() {
            errors = new HashMap<>();
        }

        public Builder putIfFails(Optional<ValidationError> validationError) {
            validationError.ifPresent(error -> errors.put(error.getFieldName(), error));
            return this;
        }

        public Builder putEachFailed(List<ValidationError> validationErrors) {
            validationErrors.forEach(error -> errors.put(error.getFieldName(), error));
            return this;
        }

        public Builder putIfFails(Supplier<Optional<ValidationError>> validator) {
            return putIfFails(validator.get());
        }

        public Builder combineWith(Builder other, String fieldPrefix) {
            other.errors.forEach((field, value) -> errors.put(fieldPrefix + SEPARATOR + field, value));
            return this;
        }

        public Builder extendWith(Builder other) {
            this.errors.putAll(other.errors);
            return this;
        }

        public Builder combineWithIndices(ArrayList<Builder> builders, String fieldPrefix) {
            for (int i=0; i<builders.size(); i++) {
                combineWith(builders.get(i), fieldPrefix + SEPARATOR + i);
            }
            return this;
        }

        public ValidationErrors build() {
            return new ValidationErrors(this.errors);
        }
    }
}
