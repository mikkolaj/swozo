package com.swozo.api.web.sandbox.dto;

import com.swozo.api.web.course.dto.CourseDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record ServiceModuleSandboxDto(
        @Schema(required = true) CourseDetailsDto courseDetailsDto,
        @Schema(required = true) List<SandboxUserDetailsDto> sandboxStudents,
        @Schema(required = true) LocalDateTime validTo
) {
}
