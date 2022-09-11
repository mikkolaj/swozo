package com.swozo.api.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDetailsDto(
        @Schema(required = true) String name,
        @Schema(required = true) String surname,
        @Schema(required = true) String email
) {
}
