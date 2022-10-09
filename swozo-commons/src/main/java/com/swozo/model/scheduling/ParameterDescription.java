package com.swozo.model.scheduling;

import com.swozo.model.scheduling.properties.FieldType;

public record ParameterDescription(
        String name,
        boolean required,
        FieldType type
) {
}
