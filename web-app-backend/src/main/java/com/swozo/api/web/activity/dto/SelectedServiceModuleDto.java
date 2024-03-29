package com.swozo.api.web.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SelectedServiceModuleDto(
        @Schema(required = true) Long serviceModuleId,
        @Schema(required = true) boolean linkConfirmationRequired
) {
}
