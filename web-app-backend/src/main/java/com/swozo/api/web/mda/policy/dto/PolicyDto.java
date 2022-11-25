package com.swozo.api.web.mda.policy.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.persistence.mda.policies.PolicyType;
import io.swagger.v3.oas.annotations.media.Schema;

public record PolicyDto(
    @Schema(required = true) Long id,
    @Schema(required = true) PolicyType policyType,
    @Schema(required = true) UserDetailsDto user,
    @Schema(required = true) Integer value
){}