package com.swozo.model.utils;

import io.swagger.v3.oas.annotations.media.Schema;

public record InstructionDto(
        @Schema(required = true) String untrustedPossiblyDangerousHtml
) {
}
