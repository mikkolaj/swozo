package com.swozo.mapper;

import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.course.dto.ParticipantDetailsDto;
import com.swozo.api.web.mda.policy.dto.PolicyDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleSummaryDto;
import com.swozo.api.web.user.RoleRepository;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.dto.UserAdminDetailsDto;
import com.swozo.api.web.user.dto.UserAdminSummaryDto;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.api.web.user.request.CreateUserRequest;
import com.swozo.model.users.ActivityRole;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.persistence.user.Role;
import com.swozo.persistence.user.User;
import com.swozo.persistence.user.UserCourseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoleRepository roleRepository;

    public abstract UserDetailsDto toDto(User user);

    @Mapping(target = "participant", expression = "java(toDto(userCourseData.getUser()))")
    public abstract ParticipantDetailsDto toDto(UserCourseData userCourseData);

    @Mapping(target = "roles", expression = "java(rolesToPersistence(createUserRequest.roles()))")
    public abstract User toPersistence(CreateUserRequest createUserRequest);

    @Mapping(target = "roles", expression = "java(rolesToDto(user.getRoles()))")
    public abstract UserAdminSummaryDto toAdminSummaryDto(User user);

    @Mapping(target = "roles", expression = "java(rolesToDto(user.getRoles()))")
    public abstract UserAdminDetailsDto userAdminDetailsDto(
            User user,
            long storageUsageBytes,
            List<CourseSummaryDto> attendedCourses,
            List<CourseSummaryDto> createdCourses,
            List<ServiceModuleSummaryDto> createdModules,
            List<PolicyDto> userPolicies
    );

    public Role roleToPersistence(RoleDto roleDto) {
        return roleRepository.findByName(roleDto.toString());
    }

    public List<Role> rolesToPersistence(List<RoleDto> roles) {
        return roles.stream().map(this::roleToPersistence).toList();
    }

    protected List<RoleDto> rolesToDto(Collection<Role> roles) {
        return roles.stream().map(this::roleToDto).toList();
    }
    public abstract OrchestratorUserDto toOrchestratorDto(User user, ActivityRole role);

    public RoleDto roleToDto(Role role) {
        return switch (role.getName().toUpperCase()) {
            case "STUDENT" -> RoleDto.STUDENT;
            case "TEACHER" -> RoleDto.TEACHER;
            case "TECHNICAL_TEACHER" -> RoleDto.TECHNICAL_TEACHER;
            case "ADMIN" -> RoleDto.ADMIN;
            default -> throw new IllegalArgumentException("Invalid App role: " + role);
        };
    }
}
