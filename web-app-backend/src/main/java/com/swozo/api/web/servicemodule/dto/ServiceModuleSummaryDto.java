package com.swozo.api.web.servicemodule.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ServiceModuleSummaryDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) String serviceName,
        @Schema(required = true) UserDetailsDto creator,
        @Schema(required = true) LocalDateTime createdAt,
        @Schema(required = true) int usedInActivitesCount
) {
}
