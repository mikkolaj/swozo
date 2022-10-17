package com.swozo.api.web.servicemodule;

import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.persistence.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceModuleValidator {

    public void validateReservation(User creator, ServiceConfig serviceConfig, ReserveServiceModuleRequest request) {
        validateAllRequiredParamsPresent(serviceConfig, request.dynamicProperties());
    }

    private void validateAllRequiredParamsPresent(ServiceConfig serviceConfig, Map<String, ?> providedParams) {
        var requiredParams = serviceConfig.parameterDescriptions().stream()
                .filter(ParameterDescription::required)
                .toList();

        if (requiredParams.stream().anyMatch(param -> !providedParams.containsKey(param.name()))) {
            throw new RuntimeException("Missing required param"); // TODO
        }
    }
}
