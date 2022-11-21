package com.swozo.api.web.servicemodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.exceptions.types.servicemodule.ServiceModuleNotFoundException;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleReservationDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleSummaryDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleUsageDto;
import com.swozo.api.web.servicemodule.dynamic.DynamicPropertiesHelper;
import com.swozo.api.web.servicemodule.request.FinishServiceModuleCreationRequest;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.api.web.user.UserRepository;
import com.swozo.mapper.ServiceModuleMapper;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
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
    private final ActivityModuleRepository activityModuleRepository;

    public Collection<ServiceModuleSummaryDto> getServiceModuleList() {
        return serviceModuleRepository.findAll().stream().map(serviceModuleMapper::toSummaryDto).toList();
    }

    public ServiceModuleDetailsDto getServiceModuleInfo(Long serviceModuleId) {
        var serviceModule = getById(serviceModuleId);
        var serviceConfig = orchestratorService.getServiceConfig(serviceModule.getServiceName());
        return serviceModuleMapper.toDto(serviceModule, serviceConfig);
    }

    public ServiceModuleSummaryDto getServiceModuleSummary(Long serviceModuleId) {
        return serviceModuleMapper.toSummaryDto(getById(serviceModuleId));
    }

    public List<ServiceModuleSummaryDto> getAllPublicModules() {
        return serviceModuleRepository.getAllByIsPublicTrueAndReadyIsTrue()
                .stream()
                .map(serviceModuleMapper::toSummaryDto)
                .toList();
    }

    public List<ServiceModuleSummaryDto> getModulesCreatedByTeacherSummary(Long teacherId) {
        return serviceModuleRepository.getAllModulesCreatedBy(teacherId)
                .stream()
                .filter(ServiceModule::isReady)
                .map(serviceModuleMapper::toSummaryDto)
                .toList();
    }

    public ServiceModule getById(Long serviceModuleId) {
        return serviceModuleRepository.findById(serviceModuleId)
                .orElseThrow(() -> ServiceModuleNotFoundException.of(serviceModuleId));
    }

    public List<ServiceModuleUsageDto> getServiceUsageInfo(Long serviceModuleId, Long userId, Long offset, Long limit) {
        var serviceModule = getById(serviceModuleId);
        serviceModuleValidator.validateIsCreator(userId, serviceModule);

        return activityModuleRepository.getActivityModulesThatUseServiceModule(serviceModuleId, offset, limit).stream()
                .map(serviceModuleMapper::toDto)
                .toList();
    }

    public ReserveServiceModuleRequest getFormDataForEdit(Long serviceModuleId, Long editorId) {
        var serviceModule = getById(serviceModuleId);
        serviceModuleValidator.validateIsCreator(editorId, serviceModule);

        return serviceModuleMapper.toFormDataDto(serviceModule);
    }

    @Transactional
    public ServiceModuleReservationDto initServiceModuleCreation(Long creatorId, ReserveServiceModuleRequest request) {
        var serviceConfig = orchestratorService.getServiceConfig(request.serviceName());
        var creator = userRepository.findById(creatorId).orElseThrow();
        serviceModuleValidator.validateReservation(creator, serviceConfig, request, false);

        var serviceModuleReservation = serviceModuleRepository.save(
                serviceModuleMapper.toPersistenceReservation(request, creator, serviceConfig)
        );

        var additionalFieldActions = handleDynamicFieldTypesForReservation(
                serviceModuleReservation, request, serviceConfig);

        return serviceModuleMapper.toReservationDto(serviceModuleReservation, additionalFieldActions);
    }

    @Transactional
    public ServiceModuleDetailsDto finishServiceModuleCreation(Long creatorId, FinishServiceModuleCreationRequest request) {
        var reservation = serviceModuleRepository.findById(request.reservationId())
                .orElseThrow(() -> ServiceModuleNotFoundException.ofReservation(request.reservationId()));
        var serviceConfig = orchestratorService.getServiceConfig(reservation.getServiceName());
        if (!reservation.getCreator().getId().equals(creatorId)) {
            throw new UnauthorizedException("you are not a creator");
        }
        if (reservation.getReady()) {
            // TODO: compare action results, if different throw
            return serviceModuleMapper.toDto(reservation, serviceConfig);
        }

        var dynamicProperties = handleDynamicFieldTypesForCreation(reservation, request, serviceConfig);

        reservation.setDynamicProperties(dynamicProperties);
        reservation.setReady(true);
        serviceModuleRepository.save(reservation);

        return serviceModuleMapper.toDto(reservation, serviceConfig);
    }

    @Transactional
    public ServiceModuleDetailsDto updateCommonData(Long userId, Long serviceModuleId, ReserveServiceModuleRequest request) {
        var serviceModule = getByIdWithCreatorValidation(serviceModuleId, userId);
        var serviceConfig = orchestratorService.getServiceConfig(serviceModule.getServiceName());
        var editor = userRepository.findById(userId).orElseThrow();
        serviceModuleValidator.validateEditCommonFields(editor, serviceModule, serviceConfig, request);

        serviceModuleMapper.updateCommonFields(serviceModule, request);
        serviceModuleRepository.save(serviceModule);
        return serviceModuleMapper.toDto(serviceModule, serviceConfig);
    }

    @Transactional
    public Map<String, Object> initServiceConfigUpdate(Long userId, Long serviceModuleId, ReserveServiceModuleRequest request) {
        var serviceModule = getByIdWithCreatorValidation(serviceModuleId, userId);
        var serviceConfig = orchestratorService.getServiceConfig(serviceModule.getServiceName());

        return handleDynamicFieldTypesForReservation(serviceModule, request, serviceConfig);
    }

    @Transactional
    public ServiceModuleUpdateTxnPingPong finishServiceConfigUpdate(Long userId, Long serviceModuleId, FinishServiceModuleCreationRequest request) {
        var serviceModule = getByIdWithCreatorValidation(serviceModuleId, userId);
        var serviceConfig = orchestratorService.getServiceConfig(serviceModule.getServiceName());
        var changed = handleDynamicFieldTypesForCreation(serviceModule, request, serviceConfig);
        var oldValues = changed.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> serviceModule.getDynamicProperties().get(entry.getKey())
        ));

        changed.forEach((fieldName, value) -> serviceModule.getDynamicProperties().put(fieldName, value));
        serviceModuleRepository.save(serviceModule);

        // We can't clean up old data unless we are certain that txn is successfully committed, that's probably the easiest workaround
        return new ServiceModuleUpdateTxnPingPong(serviceModuleMapper.toDto(serviceModule, serviceConfig), oldValues, serviceConfig);
    }

    public void cleanupOldDataOutsideTxn(ServiceModuleUpdateTxnPingPong pingPong) {
        dynamicPropertiesHelper.handleCleanup(pingPong.oldValues, getParamsByNameMap(pingPong.serviceConfig));
    }

    @Transactional
    public ServiceModuleUpdateTxnPingPong deleteServiceModule(Long userId, Long serviceModuleId) {
        var serviceModule = getByIdWithCreatorValidation(serviceModuleId, userId);
        var serviceConfig = orchestratorService.getServiceConfig(serviceModule.getServiceName());

        serviceModuleValidator.validateDeleteRequest(serviceModule);

        var dynamicProperties = serviceModule.getDynamicProperties();
        var detailsDto = serviceModuleMapper.toDto(serviceModule, serviceConfig);

        serviceModuleRepository.delete(serviceModule);

        return new ServiceModuleUpdateTxnPingPong(detailsDto, dynamicProperties, serviceConfig);
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

    private ServiceModule getByIdWithCreatorValidation(Long serviceModuleId, Long userId) {
        var serviceModule = serviceModuleRepository.findById(serviceModuleId)
                .orElseThrow(() -> ServiceModuleNotFoundException.of(serviceModuleId));
        serviceModuleValidator.validateIsCreator(userId, serviceModule);
        return serviceModule;
    }

    public record ServiceModuleUpdateTxnPingPong(
            ServiceModuleDetailsDto serviceModule,
            Map<String, String> oldValues,
            ServiceConfig serviceConfig
    ) {}
}
