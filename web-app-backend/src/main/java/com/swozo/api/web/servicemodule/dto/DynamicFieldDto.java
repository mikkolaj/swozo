package com.swozo.api.web.servicemodule.dto;

import com.swozo.model.scheduling.ParameterDescription;
import io.swagger.v3.oas.annotations.media.Schema;

public record DynamicFieldDto(
        @Schema(required = true) String value,
        @Schema(required = true) ParameterDescription parameterDescription
) {
}
