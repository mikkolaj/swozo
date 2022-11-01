package com.swozo.api.web.user.dto;

import com.swozo.api.web.auth.dto.RoleDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record UserAdminSummaryDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) String surname,
        @Schema(required = true) String email,
        @Schema(required = true) List<RoleDto> roles,
        @Schema(required = true) LocalDateTime createdAt
) {
}
