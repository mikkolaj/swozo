package com.swozo.api.web.sandbox.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateSandboxEnvironmentRequest(
        @Schema(required = true) int studentCount,
        @Schema(required = true) int validForMinutes,
        @Schema(required = true) int resultsValidForMinutes
) {
}
