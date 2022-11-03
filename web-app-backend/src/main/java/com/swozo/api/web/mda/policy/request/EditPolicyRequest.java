package com.swozo.api.web.mda.policy.request;

import java.util.Optional;

public record EditPolicyRequest(
        Optional<String> policyType,
        Optional<Long> teacherId,
        Optional<Integer> value
) {
}
