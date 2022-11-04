package com.swozo.api.web.mda.policy.request;

import com.swozo.persistence.mda.policies.Policy;
import com.swozo.persistence.mda.policies.PolicyType;

import java.util.Optional;

public record EditPolicyRequest(
        Optional<PolicyType> policyType,
        Optional<Long> teacherId,
        Optional<Integer> value
) {
}
