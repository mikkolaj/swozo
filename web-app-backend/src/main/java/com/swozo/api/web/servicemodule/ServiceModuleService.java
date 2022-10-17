package com.swozo.api.web.servicemodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleReservationDto;
import com.swozo.api.web.servicemodule.dynamic.DynamicPropertiesHelper;
import com.swozo.api.web.servicemodule.request.FinishServiceModuleCreationRequest;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceModuleService {
    private final ServiceModuleRepository serviceModuleRepository;
    private final UserRepository userRepository;
    private final ServiceModuleMapper serviceModuleMapper;
    private final DynamicPropertiesHelper dynamicPropertiesHelper;
    private final OrchestratorService orchestratorService;
    private final ServiceModuleValidator serviceModuleValidator;

    public Collection<ServiceModuleDetailsDto> getServiceModuleList() {
        return serviceModuleRepository.findAll().stream().map(serviceModuleMapper::toDto).toList();
    }

    public ServiceModuleDetailsDto getServiceModuleInfo(Long serviceModuleId) {
        var serviceModule = serviceModuleRepository.getById(serviceModuleId);
        return serviceModuleMapper.toDto(serviceModule);
    }

    @Transactional
    public ServiceModuleReservationDto reserveServiceModuleCreation(Long creatorId, ReserveServiceModuleRequest request) {
        var serviceConfig = orchestratorService.getServiceConfig(request.scheduleTypeName());
        var creator = userRepository.findById(creatorId).orElseThrow();
        serviceModuleValidator.validateReservation(creator, serviceConfig, request);

        var serviceModuleReservation = serviceModuleRepository.save(
                serviceModuleMapper.toPersistenceReservation(request, creator)
        );

        var additionalFieldActions = handleDynamicFieldTypesForReservation(
                serviceModuleReservation, request, serviceConfig);

        return serviceModuleMapper.toReservationDto(serviceModuleReservation, additionalFieldActions);
    }

    @Transactional
    public ServiceModuleDetailsDto finishServiceModuleCreation(Long creatorId, FinishServiceModuleCreationRequest request) {
        var reservation = serviceModuleRepository.findById(request.reservationId()).orElseThrow(); // TODO err
        var serviceConfig = orchestratorService.getServiceConfig(reservation.getScheduleTypeName());
        if (!reservation.getCreator().getId().equals(creatorId)) {
            throw new RuntimeException("you are not a creator");
        }
        if (reservation.getIsReady()) {
            // TODO compare action results, if different throw else return result
        }

        var dynamicProperties = handleDynamicFieldTypesForCreation(reservation, request, serviceConfig);

        reservation.setDynamicProperties(dynamicProperties);
        reservation.setIsReady(true);
        reservation.setScheduleTypeVersion(serviceConfig.version());
        serviceModuleRepository.save(reservation);

        return serviceModuleMapper.toDto(reservation);
    }

    private Map<String, Object> handleDynamicFieldTypesForReservation(
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            ServiceConfig serviceConfig
    ) {
        var result = new HashMap<String, Object>();
        var paramsByName = getParamsByNameMap(serviceConfig);

        for (var entry : request.dynamicProperties().entrySet()) {
            var paramDescription = Optional.ofNullable(paramsByName.get(entry.getKey())).orElseThrow();

            dynamicPropertiesHelper.handleFieldForReservation(
                    entry.getKey(), serviceModuleReservation, request, paramDescription
            ).ifPresent(additionalAction -> result.put(entry.getKey(), additionalAction));
        }

        return result;
    }

    private Map<String, String> handleDynamicFieldTypesForCreation(
            ServiceModule serviceModuleReservation,
            FinishServiceModuleCreationRequest request,
            ServiceConfig serviceConfig
    ) {
        var result = new HashMap<String, String>();
        var paramsByName = getParamsByNameMap(serviceConfig);

        for (var entry : request.finalDynamicFieldValues().entrySet()) {
            var paramDescription = Optional.ofNullable(paramsByName.get(entry.getKey())).orElseThrow();

            result.put(entry.getKey(), dynamicPropertiesHelper.handleFieldForCreation(
                    entry.getKey(), serviceModuleReservation, request, paramDescription));
        }

        return result;
    }

    private Map<String, ParameterDescription> getParamsByNameMap(ServiceConfig serviceConfig) {
        return serviceConfig.parameterDescriptions().stream()
                .collect(Collectors.toMap(
                    ParameterDescription::name,
                    Function.identity()
                ));
    }
}
