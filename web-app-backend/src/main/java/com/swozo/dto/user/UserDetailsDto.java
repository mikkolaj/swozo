package com.swozo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDetailsDto(
        @Schema(required = true) String name,
        @Schema(required = true) String surname,
        @Schema(required = true) String email
) {
}
