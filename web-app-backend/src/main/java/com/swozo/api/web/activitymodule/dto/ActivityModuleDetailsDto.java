package com.swozo.api.web.activitymodule.dto;

import com.swozo.api.web.activity.dto.ServiceConnectionDetailsDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ActivityModuleDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) ServiceModuleDetailsDto module,
        @Schema(required = true) String instruction,
        @Schema(required = true) List<ServiceConnectionDetailsDto> connectionDetails
) {
}
