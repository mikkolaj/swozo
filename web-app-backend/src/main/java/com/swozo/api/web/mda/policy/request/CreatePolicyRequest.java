package com.swozo.api.web.mda.policy.request;

import com.swozo.persistence.mda.policies.PolicyType;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreatePolicyRequest(
        @Schema(required = true) PolicyType policyType,
        @Schema(required = true) Long teacherId,
        @Schema(required = true) Integer value
) {
}
