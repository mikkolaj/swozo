package com.swozo.model.scheduling;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ServiceConfig(
        @Schema(required = true) String serviceName,
        @Schema(required = true) String version,
        @Schema(required = true) List<ParameterDescription> parameterDescriptions
) {
}
