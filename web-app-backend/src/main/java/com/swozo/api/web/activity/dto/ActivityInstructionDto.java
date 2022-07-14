package com.swozo.api.web.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ActivityInstructionDto(
        @Schema(required = false) String header,
        @Schema(required = true) String body
) {
}
