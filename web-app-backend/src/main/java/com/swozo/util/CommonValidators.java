package com.swozo.util;

import com.swozo.api.exceptions.types.common.ValidationError;
import com.swozo.api.exceptions.types.common.ValidationErrorType;
import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.util.Optional;
import java.util.function.Supplier;

public class CommonValidators {
    private CommonValidators() {}

    public static Optional<ValidationError> presentAndNotEmpty(String fieldName, String field) {
        return StringUtils.isEmpty(field) ?
                Optional.of(ValidationErrorType.MISSING.forField(fieldName)) : Optional.empty();
    }

    public static Optional<ValidationError> unique(String fieldName, Supplier<Optional<?>> checker) {
        return checker.get().map(val -> ValidationErrorType.NOT_UNIQUE.forField(fieldName));
    }

    public static Optional<ValidationError> timeDeltaInBounds(
            String fieldName,
            LocalDateTime start,
            LocalDateTime end,
            Duration minDelta,
            Duration maxDelta
    ) {
        var delta = Duration.between(start, end);
        if (delta.isNegative())
            return Optional.of(ValidationErrorType.START_TIME_AFTER_END.forField(fieldName));
        if (delta.compareTo(minDelta) < 0)
            return Optional.of(ValidationErrorType.TOO_SHORT_DURATION.forField(fieldName));
        if (delta.compareTo(maxDelta) > 0)
            return Optional.of(ValidationErrorType.TOO_LONG_DURATION.forField(fieldName));

        return Optional.empty();
    }

    public static Optional<ValidationError> isInFuture(String fieldName, LocalDateTime dateTime, Duration minimumOffset) {
        return Duration.between(LocalDateTime.now(), dateTime).compareTo(minimumOffset) < 0 ?
                Optional.of(ValidationErrorType.TOO_SOON.forField(fieldName)) : Optional.empty();
    }
}
