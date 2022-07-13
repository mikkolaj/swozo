package com.swozo.dto.activitymodule;

import com.swozo.dto.activity.ServiceConnectionDetailsDto;
import com.swozo.dto.servicemodule.ServiceModuleDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ActivityModuleDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) ServiceModuleDetailsDto module,
        @Schema(required = true) String instruction,
        @Schema(required = true) List<ServiceConnectionDetailsDto> connectionDetails
) {
}
