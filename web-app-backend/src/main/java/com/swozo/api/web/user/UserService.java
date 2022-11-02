package com.swozo.api.web.user;

import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.exceptions.types.user.UserNotFoundException;
import com.swozo.api.web.user.dto.UserAdminDetailsDto;
import com.swozo.api.web.user.dto.UserAdminSummaryDto;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.api.web.user.request.CreateUserRequest;
import com.swozo.mapper.UserMapper;
import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;

    public UserDetailsDto getUserInfo(Long userId) {
        return userMapper.toDto(getUserById(userId));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.of(userId));
    }

    public UserAdminDetailsDto getUserDetailsForAdmin(Long userId) {
        return userMapper.userAdminDetailsDto(getUserById(userId));
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
        return userMapper.userAdminDetailsDto(user);
    }

    public User createUserInternally(String name, String surname, String email, String password, List<RoleDto> roles) {
        return userRepository.save(
                new User(
                    name,
                    surname,
                    email,
                    authService.hashPassword(password),
                    userMapper.rolesToPersistence(roles)
                )
        );
    }

    @Transactional
    public void removeUsers(List<Long> userIds) {
        // TODO: remove all files
        userRepository.deleteAllById(userIds);
    }
}
