package com.swozo.api.web.servicemodule;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.exceptions.types.common.ValidationErrorType;
import com.swozo.api.web.exceptions.types.common.ValidationErrors;
import com.swozo.api.web.exceptions.types.common.ValidationNames;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.mapper.ServiceModuleMapper;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.user.User;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

import static com.swozo.util.CommonValidators.*;

@Service
@RequiredArgsConstructor
public class ServiceModuleValidator {
    private final ActivityModuleRepository activityModuleRepository;
    private final ServiceModuleRepository serviceModuleRepository;
    private final ServiceModuleMapper serviceModuleMapper;

    public void validateReservation(User creator, ServiceConfig serviceConfig, ReserveServiceModuleRequest request, boolean editMode) {
        var serviceParamsValidator = ValidationErrors.builder()
                .putEachFailed(allSchemaRequiredFieldsPresent(request));

        if (!editMode) {
            serviceParamsValidator
                .putIfFails(
                        unique(ValidationNames.Fields.NAME, serviceModuleRepository.findByName(request.name()))
                )
                .combineWith(
                    validateAllRequiredParamsPresent(serviceConfig, request.dynamicProperties()),
                    ValidationNames.Fields.DYNAMIC_FIELDS
                );
        }

        var mdaValidator = ValidationErrors.builder()
                .putEachFailed(allSchemaRequiredFieldsPresent(request.mdaData()))
                .extendWith(validateMdaData(serviceConfig, request));

        ValidationErrors.builder()
                .combineWith(serviceParamsValidator, ValidationNames.Fields.MODULE_VALUES)
                .combineWith(mdaValidator, ValidationNames.Fields.MDA_VALUES)
                .build()
                .throwIfAnyPresent("Invalid service module data");
    }

    public void validateEditCommonFields(
            User editor,
            ServiceModule serviceModule,
            ServiceConfig serviceConfig,
            ReserveServiceModuleRequest request
    ) {
        if (!editor.equals(serviceModule.getCreator())) {
            throw new UnauthorizedException("you are not allowed to edit this service module");
        }
        validateReservation(editor, serviceConfig, request, true);

        ValidationErrors.builder().combineWith(
            ValidationErrors.builder().putIfFails(
                    unique(ValidationNames.Fields.NAME,
                            serviceModuleRepository.findByName(request.name())
                                    .filter(serviceModuleWithSameName -> !serviceModule.equals(serviceModuleWithSameName))

                    )
            ), ValidationNames.Fields.MODULE_VALUES
        )
        .build()
        .throwIfAnyPresent("Invalid service module data");
    }

    public void validateIsCreator(Long userId, ServiceModule serviceModule) {
        if (!serviceModule.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to view usage for service " + serviceModule.getId());
        }
    }

    public void validateDeleteRequest(ServiceModule serviceModule) {
        if (activityModuleRepository.countActivityModulesByServiceModuleId(serviceModule.getId()) > 0) {
            throw new UnauthorizedException("You can't delete used service module");
        }
    }

    private ValidationErrors.Builder validateAllRequiredParamsPresent(ServiceConfig serviceConfig, Map<String, ?> providedParams) {
        var builder = ValidationErrors.builder();
        serviceConfig.parameterDescriptions().stream()
                .filter(ParameterDescription::required)
                .filter(parameterDescription -> !providedParams.containsKey(parameterDescription.name()))
                .forEach(parameterDescription -> builder.putIfFails(
                            Optional.of(ValidationErrorType.MISSING.forField(parameterDescription.name()))
                        )
                );
        return builder;
    }

    private ValidationErrors.Builder validateMdaData(ServiceConfig serviceConfig, ReserveServiceModuleRequest request) {
        var mda = request.mdaData();
        var builder = ValidationErrors.builder()
                .putIfFails(
                        contains(
                                ValidationNames.Fields.ISOLATION_MODE,
                                serviceConfig.isolationModes(),
                                serviceModuleMapper.from(request.mdaData().isIsolated())
                        )
                )
                .putEachFailed(
                        allPositive(
                                Map.of(
                                        ValidationNames.Fields.BASE_VCPU, mda.baseVcpu(),
                                        ValidationNames.Fields.BASE_RAM, mda.baseRam(),
                                        ValidationNames.Fields.BASE_DISK, mda.baseDisk(),
                                        ValidationNames.Fields.BASE_BANDWIDTH, mda.baseBandwidth()
                                )
                ));

        mda.sharedServiceModuleMdaDto().ifPresent(sharedMda -> {
            builder.combineWith(
                ValidationErrors.builder().putEachFailed(
                    allPositive(
                            Map.of(
                                    ValidationNames.Fields.USERS_PER_ADDITIONAL_CORE, sharedMda.usersPerAdditionalCore(),
                                    ValidationNames.Fields.USERS_PER_ADDITIONAL_RAM_GB, sharedMda.usersPerAdditionalRamGb(),
                                    ValidationNames.Fields.USERS_PER_ADDITIONAL_DISK_GB, sharedMda.usersPerAdditionalDiskGb(),
                                    ValidationNames.Fields.USERS_PER_ADDITIONAL_BANDWIDTH_GBS, sharedMda.usersPerAdditionalBandwidthGbps()
                            )
                    )
            ), ValidationNames.Fields.SHARED_SERVICE_MODULE_MDA_DTO);
        });

        return builder;
    }
}
