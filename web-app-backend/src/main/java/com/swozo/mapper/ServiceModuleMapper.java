package com.swozo.mapper;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleReservationDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleSummaryDto;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.persistence.ServiceModule;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

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

    @Mapping(target = "creator", expression = "java(userMapper.toDto(serviceModule.getCreator()))")
    @Mapping(target = "serviceName", source = "scheduleTypeName")
    @Mapping(target = "usedInActivitiesCount", expression = "java(activityModuleRepository.countActivityModulesByServiceModuleId(serviceModule.getId()))")
    @Mapping(target = "teacherInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getTeacherInstructionHtml()))")
    @Mapping(target = "studentInstruction", expression = "java(commonMappers.instructionToDto(serviceModule.getStudentInstructionHtml()))")
    public abstract ServiceModuleDetailsDto toDto(ServiceModule serviceModule);

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

    public ServiceModuleReservationDto toReservationDto(ServiceModule serviceModule, Map<String, Object> dynamicFieldAdditionalData) {
        return new ServiceModuleReservationDto(serviceModule.getId(), dynamicFieldAdditionalData);
    }
}
