package com.swozo.api.web.servicemodule.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ReserveServiceModuleRequest(
        @Schema(required = true) String name,
        @Schema(required = true) String instructionsFromTechnicalTeacher,
        @Schema(required = true) String subject,
        @Schema(required = true) String scheduleTypeName,
        @Schema(required = true) Map<String, String> dynamicProperties,
        @Schema(required = true) Boolean isPublic
) {
}
