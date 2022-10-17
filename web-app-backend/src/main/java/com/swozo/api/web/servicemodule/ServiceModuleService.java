package com.swozo.api.web.servicemodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleReservationDto;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.api.web.user.UserRepository;
import com.swozo.mapper.ServiceModuleMapper;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.persistence.ServiceModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceModuleService {
    private final ServiceModuleRepository serviceModuleRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper serviceModuleMapper;
    private final DynamicPropertiesHelper dynamicPropertiesHelper;
    private final OrchestratorService orchestratorService;

    public Collection<ServiceModuleDetailsDto> getServiceModuleList() {
        return serviceModuleRepository.findAll().stream().map(serviceModuleMapper::toDto).toList();
    }

    public ServiceModuleDetailsDto getServiceModuleInfo(Long serviceModuleId) {
        var serviceModule = serviceModuleRepository.getById(serviceModuleId);
        return serviceModuleMapper.toDto(serviceModule);
    }

    @Transactional
    public ServiceModuleReservationDto reserveServiceModuleCreation(Long creatorId, ReserveServiceModuleRequest request) {
        // TODO: validation
        var serviceConfig = orchestratorService.getServiceConfig(request.scheduleTypeName());
        var creator = userRepository.findById(creatorId).orElseThrow();
        var serviceModuleReservation = serviceModuleRepository.save(
                serviceModuleMapper.toPersistenceReservation(request, creator)
        );

        var additionalFieldActions = handleDynamicFieldTypes(serviceModuleReservation, request, serviceConfig);
        return serviceModuleMapper.toReservationDto(serviceModuleReservation, additionalFieldActions);
    }

    private Map<String, Object> handleDynamicFieldTypes(
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            ServiceConfig serviceConfig
    ) {
        var result = new HashMap<String, Object>();
        var requiredParams = serviceConfig.parameterDescriptions().stream()
                .filter(ParameterDescription::required)
                .toList();

        if (requiredParams.stream().anyMatch(param -> !request.dynamicProperties().containsKey(param.name()))) {
            throw new RuntimeException("Missing required param"); // TODO
        }

        for (var entry : request.dynamicProperties().entrySet()) {
            var paramDescription = serviceConfig.parameterDescriptions().stream()
                    .filter(param -> param.name().equals(entry.getKey()))
                    .findAny()
                    .orElseThrow(); // TODO: unknown param

            dynamicPropertiesHelper.handleFieldForReservation(
                    serviceModuleReservation, request, entry.getValue(), paramDescription
            ).ifPresent(additionalAction -> result.put(entry.getKey(), additionalAction));
        }

        return result;
    }
}
