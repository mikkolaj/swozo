package com.swozo.api.web.user.dto;

import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.mda.policy.dto.PolicyDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleSummaryDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record UserAdminDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) String surname,
        @Schema(required = true) String email,
        @Schema(required = true) List<RoleDto> roles,
        @Schema(required = true) LocalDateTime createdAt,
        @Schema(required = true) long storageUsageBytes,
        @Schema(required = true) List<CourseSummaryDto> attendedCourses,
        @Schema(required = true) List<CourseSummaryDto> createdCourses,
        @Schema(required = true) List<ServiceModuleSummaryDto> createdModules,
        @Schema(required = true) List<PolicyDto> userPolicies
        ) {
}
