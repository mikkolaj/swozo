package com.swozo.util;

import com.swozo.api.web.exceptions.types.common.ValidationError;
import com.swozo.api.web.exceptions.types.common.ValidationErrorType;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommonValidators {
    private CommonValidators() {}

    public static Optional<ValidationError> presentAndNotEmpty(String fieldName, String field) {
        return StringUtils.isEmpty(field) ?
                Optional.of(ValidationErrorType.MISSING.forField(fieldName)) : Optional.empty();
    }

    public static Optional<ValidationError> numberInBounds(String fieldName, int value, int minInclusive, int maxInclusive) {
        return value > maxInclusive || value < minInclusive ?
            Optional.of(ValidationErrorType.NOT_IN_BOUNDS.forField(fieldName)) : Optional.empty();
    }

    public static List<ValidationError> allSchemaRequiredFieldsPresent(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .map(field ->
                    Arrays.stream(field.getAnnotations())
                            .filter(annotation -> annotation.annotationType().equals(Schema.class))
                            .map(annotation -> (Schema) annotation)
                            .filter(Schema::required)
                            .findAny()
                            .flatMap(required -> {
                                try {
                                    field.setAccessible(true);
                                    return field.get(obj) == null ?
                                            Optional.of(ValidationErrorType.MISSING.forField(field.getName())) : Optional.empty();
                                } catch (IllegalAccessException e) {
                                    return Optional.empty();
                                }
                            })
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
    }

    public static Optional<ValidationError> unique(String fieldName, Optional<?> repeatedValue) {
        return repeatedValue.map(val -> ValidationErrorType.NOT_UNIQUE.forField(fieldName));
    }

    public static Optional<ValidationError> exists(String fieldName, Optional<?> repeatedValue) {
        return repeatedValue.map(val -> ValidationErrorType.DOESNT_EXIST.forField(fieldName));
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

    public static Optional<ValidationError> notOverlapping(
            String fieldName,
            LocalDateTime start1, LocalDateTime end1,
            LocalDateTime start2, LocalDateTime end2,
            Duration minimumOffset
    ) {
        if (start1.isAfter(start2))
            return notOverlapping(fieldName, start2, end2, start1, end1, minimumOffset);

        return Duration.between(end1, start2).compareTo(minimumOffset) < 0 ?
                Optional.of(ValidationErrorType.TOO_SHORT_PERIOD_BETWEEN.forField(fieldName)) : Optional.empty();
    }
}
