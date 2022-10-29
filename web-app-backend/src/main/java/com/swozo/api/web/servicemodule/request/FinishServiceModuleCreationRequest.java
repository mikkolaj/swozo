package com.swozo.api.web.servicemodule.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record FinishServiceModuleCreationRequest(
        @Schema(required = true) Long reservationId,
        @Schema(required = true) Map<String, String> echoFieldActions,
        @Schema(required = true) Map<String, String> repeatedInitialValues,
        @Schema(required = true) Map<String, String> finalDynamicFieldValues
) {
}
