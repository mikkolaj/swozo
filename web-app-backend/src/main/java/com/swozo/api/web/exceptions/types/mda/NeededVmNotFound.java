package com.swozo.api.web.exceptions.types.mda;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class NeededVmNotFound extends ApiException {
    public NeededVmNotFound(String message) {
        super(message, ErrorType.POLICY_NOT_MET);
    }

    public static NeededVmNotFound withConditions(Integer vcpu, Integer ramGB, Integer bandwidthMbps) {
        return new NeededVmNotFound("No Vm in repository which would match these conditions:  vcpu ->" + vcpu
                + " ramGB ->" + ramGB + " bandwidthMbps->" + bandwidthMbps);
    }
}
