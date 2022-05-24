package com.swozo.api.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginData(
        @Schema(required = true) String email,
        @Schema(required = true) String password) {
}
