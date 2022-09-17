package com.swozo.api.web.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ActivityInstructionDto(
        @Schema(required = true) String sanitizedHtmlData
) {
}
