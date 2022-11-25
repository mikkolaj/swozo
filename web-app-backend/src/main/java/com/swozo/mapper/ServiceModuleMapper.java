package com.swozo.mapper;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.servicemodule.dto.*;
import com.swozo.api.web.servicemodule.dynamic.DynamicPropertiesHelper;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.IsolationMode;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.servicemodule.IsolatedServiceModule;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.servicemodule.SharedServiceModule;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ServiceModuleMapper {
    @Autowired
    protected ServiceModuleRepository serviceModuleRepository;
    @Autowired
    protected ActivityModuleRepository activityModuleRepository;
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected CommonMappers commonMappers;
    @Autowired
    protected DynamicPropertiesHelper dynamicPropertiesHelper;

    protected Map<String, DynamicFieldDto> dynamicFieldsToDto(ServiceModule serviceModule, ServiceConfig serviceConfig) {
        var paramsByNameMap = serviceConfig.parameterDescriptions().stream()
                .collect(Collectors.toMap(
                    ParameterDescription::name,
                    Function.identity()
                )
        );
        return serviceModule.getDynamicProperties().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> {
                            var paramDescription = paramsByNameMap.get(e.getKey());
                            return new DynamicFieldDto(
                                    dynamicPropertiesHelper.decodeValue(e.getValue(), paramDescription),
                                    paramDescription
                            );
                        }
                ));
    }

    protected abstract SharedServiceModuleMdaDto sharedMdaToDto(SharedServiceModule serviceModule);

    @Mapping(target = "isIsolated", expression = "java(sharedServiceModuleMdaDto.isEmpty())")
    protected abstract ServiceModuleMdaDto mdaToDto(ServiceModule serviceModule, Optional<SharedServiceModuleMdaDto> sharedServiceModuleMdaDto);

    protected ServiceModuleMdaDto mdaToDto(ServiceModule serviceModule) {
        return mdaToDto(serviceModule, serviceModule.isIsolated() ?
                Optional.empty() : Optional.of(sharedMdaToDto((SharedServiceModule) serviceModule)));
    }

    public IsolationMode from(boolean isIsolated) {
        return isIsolated ? IsolationMode.ISOLATED : IsolationMode.SHARED;
    }

    @Mapping(target = "creator", expression = "java(userMapper.toDto(serviceModule.getCreator()))")
    @Mapping(target = "serviceName", source = "serviceModule.serviceName")
    @Mapping(target = "usedInActivitiesCount", expression = "java(activityModuleRepository.countActivityModulesByServiceModuleId(serviceModule.getId()))")
    @Mapping(target = "teacherInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getTeacherInstructionHtml()))")
    @Mapping(target = "studentInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getStudentInstructionHtml()))")
    @Mapping(target = "dynamicFields", expression = "java(dynamicFieldsToDto(serviceModule, serviceConfig))")
    @Mapping(target = "serviceModuleMdaDto", expression = "java(mdaToDto(serviceModule))")
    public abstract ServiceModuleDetailsDto toDto(ServiceModule serviceModule, ServiceConfig serviceConfig);

    @Mapping(target = "creator", expression = "java(userMapper.toDto(serviceModule.getCreator()))")
    @Mapping(target = "usedInActivitiesCount", expression = "java(activityModuleRepository.countActivityModulesByServiceModuleId(serviceModule.getId()))")
    @Mapping(target = "teacherInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getTeacherInstructionHtml()))")
    @Mapping(target = "studentInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getStudentInstructionHtml()))")
    public abstract ServiceModuleSummaryDto toSummaryDto(ServiceModule serviceModule);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dynamicProperties", ignore = true)
    @Mapping(target = "ready", expression = "java(false)")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "creator", expression = "java(creator)")
    @Mapping(target = "teacherInstructionHtml", expression = "java(commonMappers.instructionToPersistence(request.teacherInstruction()))")
    @Mapping(target = "studentInstructionHtml", expression = "java(commonMappers.instructionToPersistence(request.studentInstruction()))")
    @Mapping(target = "baseVcpu", source = "request.mdaData.baseVcpu")
    @Mapping(target = "baseRamGB", source = "request.mdaData.baseRamGB")
    @Mapping(target = "baseDiskGB", source = "request.mdaData.baseDiskGB")
    @Mapping(target = "baseBandwidthMbps", source = "request.mdaData.baseBandwidthMbps")
    protected abstract IsolatedServiceModule toIsolatedPersistenceReservation(ReserveServiceModuleRequest request, User creator);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dynamicProperties", ignore = true)
    @Mapping(target = "ready", expression = "java(false)")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "creator", expression = "java(creator)")
    @Mapping(target = "teacherInstructionHtml", expression = "java(commonMappers.instructionToPersistence(request.teacherInstruction()))")
    @Mapping(target = "studentInstructionHtml", expression = "java(commonMappers.instructionToPersistence(request.studentInstruction()))")
    @Mapping(target = "baseVcpu", source = "request.mdaData.baseVcpu")
    @Mapping(target = "baseRamGB", source = "request.mdaData.baseRamGB")
    @Mapping(target = "baseDiskGB", source = "request.mdaData.baseDiskGB")
    @Mapping(target = "baseBandwidthMbps", source = "request.mdaData.baseBandwidthMbps")
    @Mapping(target = "usersPerAdditionalCore", source = "sharedMdaData.usersPerAdditionalCore")
    @Mapping(target = "usersPerAdditionalRamGb", source = "sharedMdaData.usersPerAdditionalRamGb")
    @Mapping(target = "usersPerAdditionalDiskGb", source = "sharedMdaData.usersPerAdditionalDiskGb")
    @Mapping(target = "usersPerAdditionalBandwidthGbps", source = "sharedMdaData.usersPerAdditionalBandwidthGbps")
    protected abstract SharedServiceModule toSharedPersistenceReservation(
            ReserveServiceModuleRequest request, SharedServiceModuleMdaDto sharedMdaData, User creator);

    public ServiceModule toPersistenceReservation(ReserveServiceModuleRequest request, User creator) {
        return request.mdaData().isIsolated() ?
                toIsolatedPersistenceReservation(request,  creator) :
                toSharedPersistenceReservation(request, request.mdaData().sharedServiceModuleMdaDto().orElseThrow(), creator);
    }

    @Mapping(target = "teacherInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getTeacherInstructionHtml()))")
    @Mapping(target = "studentInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getStudentInstructionHtml()))")
    @Mapping(target = "mdaData", expression = "java(mdaToDto(serviceModule))")
    public abstract ReserveServiceModuleRequest toFormDataDto(ServiceModule serviceModule);

    public void updateCommonFields(ServiceModule serviceModule, ReserveServiceModuleRequest request) {
        // change of service type and isolation type is not supported
        serviceModule.setName(request.name());
        serviceModule.setSubject(request.subject());
        serviceModule.setDescription(request.description());
        serviceModule.setTeacherInstructionHtml(commonMappers.instructionToPersistence(request.teacherInstruction()));
        serviceModule.setStudentInstructionHtml(commonMappers.instructionToPersistence(request.studentInstruction()));
        serviceModule.setMdaData(request.mdaData());
    }

    public ServiceModuleReservationDto toReservationDto(ServiceModule serviceModule, Map<String, Object> dynamicFieldAdditionalData) {
        return new ServiceModuleReservationDto(serviceModule.getId(), dynamicFieldAdditionalData);
    }

    public ServiceModuleUsageDto toDto(ActivityModule activityModule) {
        var course = activityModule.getActivity().getCourse();
        return new ServiceModuleUsageDto(
                userMapper.toDto(course.getTeacher()),
                course.getId(),
                course.getName(),
                activityModule.getActivity().getName(),
                activityModule.getActivity().getCreatedAt()
        );
    }
}
