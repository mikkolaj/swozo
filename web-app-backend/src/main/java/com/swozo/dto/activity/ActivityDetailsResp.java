package com.swozo.dto.activity;

import com.swozo.dto.activitymodule.ActivityModuleDetailsResp;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

// TODO add module info here
public record ActivityDetailsResp(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) String description,
        @Schema(required = true) LocalDateTime startTime,
        @Schema(required = true) LocalDateTime endTime,
        @Schema(required = true) List<ActivityInstruction> instructionsFromTeacher,
        @Schema(required = true) List<ActivityModuleDetailsResp> activityModules
) {
}
