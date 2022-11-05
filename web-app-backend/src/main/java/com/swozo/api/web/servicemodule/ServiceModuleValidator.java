package com.swozo.api.web.servicemodule;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.user.User;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceModuleValidator {
    private final ActivityModuleRepository activityModuleRepository;

    public void validateReservation(User creator, ServiceConfig serviceConfig, ReserveServiceModuleRequest request) {
        validateAllRequiredParamsPresent(serviceConfig, request.dynamicProperties());
    }

    public void validateIsCreator(Long userId, ServiceModule serviceModule) {
        if (!serviceModule.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view usage for service " + serviceModule.getId());
        }
    }

    public void validateDeleteRequest(ServiceModule serviceModule) {
        if (activityModuleRepository.countActivityModulesByServiceModuleId(serviceModule.getId()) > 0) {
            throw new RuntimeException("TODO cant delete used");
        }
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
