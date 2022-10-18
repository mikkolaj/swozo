package com.swozo.api.web.servicemodule.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

// TODO add MDA data
public record ReserveServiceModuleRequest(
        @Schema(required = true) String name,
        @Schema(required = true) String instructionHtml,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) String scheduleTypeName,
        @Schema(required = true) Map<String, String> dynamicProperties,
        @Schema(required = true) Boolean isPublic
) {
}
