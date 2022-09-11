package com.swozo.api.web.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record ServiceConnectionDetailsDto(
        @Schema(required = false) String url,
        @Schema(required = true) String serviceName,
        @Schema(required = true) String connectionInstruction,
        @Schema(required = false) String connectionInfo
) {
}