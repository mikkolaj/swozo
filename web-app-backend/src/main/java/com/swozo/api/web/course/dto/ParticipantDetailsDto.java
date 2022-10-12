package com.swozo.api.web.course.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ParticipantDetailsDto(
        @Schema(required = true) UserDetailsDto participant,
        @Schema(required = true) LocalDateTime joinedAt
) {
}
