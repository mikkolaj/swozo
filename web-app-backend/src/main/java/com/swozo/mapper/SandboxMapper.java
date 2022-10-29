package com.swozo.mapper;

import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.sandbox.SandboxService;
import com.swozo.api.web.sandbox.dto.SandboxUserDetailsDto;
import com.swozo.api.web.sandbox.dto.ServiceModuleSandboxDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class SandboxMapper {

    @Mapping(target = "email", expression = "java(sandboxUser.user().getEmail())")
    @Mapping(target = "password", source = "plaintextPassword")
    public abstract SandboxUserDetailsDto toDto(SandboxService.SandboxUser sandboxUser);


    public ServiceModuleSandboxDto toDto(
            CourseDetailsDto courseDetailsDto,
            List<SandboxService.SandboxUser> sandboxUsers,
            LocalDateTime validTo
    ) {
        return new ServiceModuleSandboxDto(
                courseDetailsDto,
                sandboxUsers.stream().map(this::toDto).toList(),
                validTo
        );
    }
}
