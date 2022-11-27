package com.swozo.api.web.exceptions.types.mda;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;
import com.swozo.api.web.exceptions.types.common.ValidationNames;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.mda.policies.PolicyType;

import java.util.Map;

public class PolicyNotMetException extends ApiException {
    private PolicyNotMetException(String message, Map<String, Object> additionalData) {
        super(message, ErrorType.POLICY_NOT_MET, additionalData);
    }

    private PolicyNotMetException(String message) {
        this(message, null);
    }

    public static PolicyNotMetException withBrokenPolicy(PolicyType policyType, Integer policyValue, Integer realValue ) {
        return new PolicyNotMetException("Policy with type: " + policyType.name() + " didn't met, policy value: " +
                policyValue + " real value: " + realValue);
    }

    public PolicyNotMetException withAdditionalInfoAbout(Activity activity) {
        return new PolicyNotMetException(this.getMessage(), Map.of(ValidationNames.Errors.ACTIVITY_NAME, activity.getName()));
    }
}
