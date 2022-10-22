package com.swozo.api.web.activity.request;

import com.swozo.model.utils.InstructionDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record CreateActivityRequest(
        @Schema(required = true) String name,
        @Schema(required = true) String description,
        @Schema(required = true) LocalDateTime startTime,
        @Schema(required = true) LocalDateTime endTime,
        @Schema(required = true) InstructionDto instructionFromTeacher,
        @Schema(required = true) List<Long> selectedModulesIds
) {
}
