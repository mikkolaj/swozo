package com.swozo.dto.activity;

import io.swagger.v3.oas.annotations.media.Schema;

public record ActivityInstruction(
        @Schema(required = false) String header,
        @Schema(required = true) String body
) {
}
