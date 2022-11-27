package com.swozo.model.scheduling;

import com.swozo.model.scheduling.properties.IsolationMode;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Set;

public record ServiceConfig(
        @Schema(required = true) String serviceName,
        @Schema(required = true) List<ParameterDescription> parameterDescriptions,
        @Schema(required = true) Set<IsolationMode> isolationModes,
        @Schema(required = true) Integer provisioningSeconds
) {
}
