package com.swozo.api.web.exceptions.types.mda;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;
import com.swozo.persistence.mda.policies.PolicyType;

public class PolicyNotMetException extends ApiException {
    public PolicyNotMetException(String message) {
        super(message, ErrorType.POLICY_NOT_MET);
    }

    public static PolicyNotMetException withBrokenPolicy(PolicyType policyType, Integer policyValue, Integer realValue ) {
        return new PolicyNotMetException("Policy with type: " + policyType.name() + " didn't met, policy value: " +
                policyValue + " real value: " + realValue);
    }
}
