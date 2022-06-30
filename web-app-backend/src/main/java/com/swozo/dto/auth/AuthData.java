package com.swozo.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record AuthData(
        @Schema(required = true) String accessToken,
        @Schema(required = true) long expiresIn,
        @Schema(required = true) List<AppRole> roles) {
}
