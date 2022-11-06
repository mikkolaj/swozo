package com.swozo.api.web.servicemodule.request;

import com.swozo.model.utils.InstructionDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

// TODO add MDA data
public record ReserveServiceModuleRequest(
        @Schema(required = true) String name,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) InstructionDto teacherInstruction,
        @Schema(required = true) InstructionDto studentInstruction,
        @Schema(required = true) String scheduleTypeName,
        @Schema(required = true) Map<String, String> dynamicProperties,
        @Schema(required = true) Boolean isPublic,
        @Schema(required = true) Boolean isIsolated
) {
}
