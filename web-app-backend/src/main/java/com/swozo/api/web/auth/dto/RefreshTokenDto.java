package com.swozo.api.web.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RefreshTokenDto(
        @Schema(required = true) long tokenId,
        @Schema(required = true) long expiresIn,
        @Schema(required = true) String refreshToken
) {
}
