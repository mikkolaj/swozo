package com.swozo.api.web.activity.dto;

import com.swozo.api.common.files.dto.FileDto;
import com.swozo.api.web.activitymodule.dto.ActivityModuleDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

// TODO add module info here
public record ActivityDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) String description,
        @Schema(required = true) LocalDateTime startTime,
        @Schema(required = true) LocalDateTime endTime,
        @Schema(required = true) List<ActivityInstructionDto> instructionsFromTeacher,
        @Schema(required = true) List<ActivityModuleDetailsDto> activityModules,
        @Schema(required = true) List<FileDto> publicFiles
) {
}
