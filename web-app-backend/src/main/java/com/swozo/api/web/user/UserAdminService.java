package com.swozo.api.web.user;

import com.swozo.api.common.files.FileService;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.CourseService;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.exceptions.types.user.UserNotFoundException;
import com.swozo.api.web.servicemodule.ServiceModuleService;
import com.swozo.api.web.servicemodule.dto.ServiceModuleSummaryDto;
import com.swozo.api.web.user.dto.UserAdminDetailsDto;
import com.swozo.api.web.user.dto.UserAdminSummaryDto;
import com.swozo.api.web.user.request.CreateUserRequest;
import com.swozo.mapper.UserMapper;
import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdminService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final CourseService courseService;
    private final FileService fileService;
    private final ServiceModuleService serviceModuleService;

    public UserAdminDetailsDto getUserDetailsForAdmin(Long userId) {
        var user = getUserById(userId);

        List<CourseSummaryDto> attendedCourses = authService.hasRole(user, RoleDto.STUDENT) ?
                courseService.getUserCourseSummaries(userId, RoleDto.STUDENT) : List.of();
        List<CourseSummaryDto> createdCourses = authService.hasRole(user, RoleDto.TEACHER) ?
                courseService.getUserCourseSummaries(userId, RoleDto.TEACHER) : List.of();
        List<ServiceModuleSummaryDto> createdServiceModules = authService.hasRole(user, RoleDto.TECHNICAL_TEACHER) ?
                serviceModuleService.getModulesCreatedByTeacherSummary(userId) : List.of();

        var totalStorage = fileService.getTotalStorageUsedByUser(userId);

        return userMapper.userAdminDetailsDto(user, totalStorage, attendedCourses, createdCourses, createdServiceModules);
    }

    public UserAdminDetailsDto setUserRoles(Long userId, List<RoleDto> roles) {
        var user = getUserById(userId);
        user.setRoles(new LinkedList<>(userMapper.rolesToPersistence(roles)));
        userRepository.save(user);
        return getUserDetailsForAdmin(userId);
    }

    public List<UserAdminSummaryDto> getUsersForAdmin() {
        return userRepository.findAll().stream()
                .map(userMapper::toAdminSummaryDto)
                .toList();
    }

    @Transactional
    public UserAdminDetailsDto createUser(CreateUserRequest request) {
        userValidator.validateCreateUserRequest(request);
        var initialPassword = authService.provideInitialPassword();

        var user = userMapper.toPersistence(request);
        user.setPassword(authService.hashPassword(initialPassword));
        user.setChangePasswordToken(authService.provideChangePasswordToken());

        logger.info("Created User {}. Change password uuid is: {}, initial password is: {}",
                user.getEmail(), user.getChangePasswordToken(), initialPassword);

        userRepository.save(user);
        return userMapper.userAdminDetailsDto(user, 0, List.of(), List.of(), List.of());
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.of(userId));
    }
}
