package com.swozo.api.web.servicemodule.dto;

import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.properties.ScheduleType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ServiceConfigDto(
        @Schema(required = true) ScheduleType scheduleType,
        @Schema(required = true) String serviceName,
        @Schema(required = true) List<ParameterDescription> parameterDescriptions
) {
}
