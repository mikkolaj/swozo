package com.swozo.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserDetailsResp(
        @Schema(required = true) String email,
        @Schema(required = true) String firstName,
        @Schema(required = true) String lastName
) {
}
