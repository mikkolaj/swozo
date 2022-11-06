package com.swozo.mapper;

import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.dto.ParticipantDetailsDto;
import com.swozo.api.web.user.RoleRepository;
import com.swozo.api.web.user.UserRepository;
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

    protected List<Role> rolesToPersistence(List<RoleDto> roles) {
        return roles.stream()
                .map(roleDto -> roleRepository.findByName(roleDto.toString()))
                .toList();
    }
}
