package com.swozo.api.web.servicemodule.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

public record ServiceModuleReservationDto(
        @Schema(required = true) Long reservationId,
        @Schema(required = true) Map<String, Object> dynamicFieldAdditionalActions
) {
}
