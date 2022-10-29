package com.swozo.api.web.user.request;

import com.swozo.api.web.auth.dto.RoleDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreateUserRequest(
        @Schema(required = true) String name,
        @Schema(required = true) String surname,
        @Schema(required = true) String password,
        @Schema(required = true) String email,
        @Schema(required = true) List<RoleDto> roles
) {
}
