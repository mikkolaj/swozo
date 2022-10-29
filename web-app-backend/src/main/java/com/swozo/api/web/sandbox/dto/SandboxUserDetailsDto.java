package com.swozo.api.web.sandbox.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record SandboxUserDetailsDto(
        @Schema(required = true) String email,
        @Schema(required = true) String password
) {
}
