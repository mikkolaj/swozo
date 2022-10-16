package com.swozo.model.scheduling;

import com.swozo.model.scheduling.properties.FieldType;
import io.swagger.v3.oas.annotations.media.Schema;

public record ParameterDescription(
        @Schema(required = true) String name,
        @Schema(required = true) boolean required,
        @Schema(required = true) FieldType type
) {
}
