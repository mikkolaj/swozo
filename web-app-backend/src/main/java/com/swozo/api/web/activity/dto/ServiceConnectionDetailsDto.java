package com.swozo.api.web.activity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

public record ServiceConnectionDetailsDto(
        @Schema(required = true) String serviceName,
        @Schema(required = true) String connectionInstruction,
        Optional<String> url,
        Optional<String> connectionInfo
) {
}
