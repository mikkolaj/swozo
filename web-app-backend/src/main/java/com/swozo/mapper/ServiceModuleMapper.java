package com.swozo.mapper;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.servicemodule.dto.*;
import com.swozo.api.web.servicemodule.dynamic.DynamicPropertiesHelper;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.persistence.ServiceModule;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
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

    @Mapping(target = "creator", expression = "java(userMapper.toDto(serviceModule.getCreator()))")
    @Mapping(target = "serviceName", source = "serviceModule.scheduleTypeName")
    @Mapping(target = "usedInActivitiesCount", expression = "java(activityModuleRepository.countActivityModulesByServiceModuleId(serviceModule.getId()))")
    @Mapping(target = "teacherInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getTeacherInstructionHtml()))")
    @Mapping(target = "studentInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getStudentInstructionHtml()))")
    @Mapping(target = "dynamicFields", expression = "java(dynamicFieldsToDto(serviceModule, serviceConfig))")
    public abstract ServiceModuleDetailsDto toDto(ServiceModule serviceModule, ServiceConfig serviceConfig);

    @Mapping(target = "creator", expression = "java(userMapper.toDto(serviceModule.getCreator()))")
    @Mapping(target = "serviceName", source = "scheduleTypeName")
    @Mapping(target = "usedInActivitiesCount", expression = "java(activityModuleRepository.countActivityModulesByServiceModuleId(serviceModule.getId()))")
    @Mapping(target = "teacherInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getTeacherInstructionHtml()))")
    @Mapping(target = "studentInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getStudentInstructionHtml()))")
    public abstract ServiceModuleSummaryDto toSummaryDto(ServiceModule serviceModule);

    @Mapping(target = "dynamicProperties", ignore = true)
    @Mapping(target = "ready", expression = "java(false)")
    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "creator", expression = "java(creator)")
    @Mapping(target = "teacherInstructionHtml", expression = "java(commonMappers.instructionToPersistence(request.teacherInstruction()))")
    @Mapping(target = "studentInstructionHtml", expression = "java(commonMappers.instructionToPersistence(request.studentInstruction()))")
    public abstract ServiceModule toPersistenceReservation(ReserveServiceModuleRequest request, User creator);

    @Mapping(target = "teacherInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getTeacherInstructionHtml()))")
    @Mapping(target = "studentInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getStudentInstructionHtml()))")
    public abstract ReserveServiceModuleRequest toFormDataDto(ServiceModule serviceModule);

    public void updateCommonFields(ServiceModule serviceModule, ReserveServiceModuleRequest request) {
        // change of service type is not supported
        serviceModule.setName(request.name());
        serviceModule.setSubject(request.subject());
        serviceModule.setDescription(request.description());
        serviceModule.setTeacherInstructionHtml(commonMappers.instructionToPersistence(request.teacherInstruction()));
        serviceModule.setStudentInstructionHtml(commonMappers.instructionToPersistence(request.studentInstruction()));
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
