package com.swozo.api.web.user;

import com.swozo.api.common.email.EmailService;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    private final UserMapper userMapper;
    private final EmailService emailService;

    public UserDetailsDto getUserInfo(Long userId) {
        var user = userRepository.findById(userId).orElseThrow();
        return userMapper.toDto(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    public UserAdminDetailsDto getUserDetailsForAdmin(Long userId) {
        var user =  userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.of(userId));
        return userMapper.userAdminDetailsDto(user);
    }

    public List<UserAdminSummaryDto> getUsersForAdmin() {
        return userRepository.findAll().stream()
                .map(userMapper::toAdminSummaryDto)
                .toList();
    }

    public UserAdminDetailsDto createUser(CreateUserRequest request) {
        userValidator.validateCreateUserRequest(request);
        var initialPassword = authService.provideInitialPassword();

        var user = userMapper.toPersistence(request);
        user.setPassword(authService.hashPassword(initialPassword));
        user.setForgotPasswordUUID(UUID.randomUUID().toString());

        logger.info("Created User {}. Change password uuid is: {}, initial password is: {}",
                user.getEmail(), user.getForgotPasswordUUID(), initialPassword);

        userRepository.save(user);
        return userMapper.userAdminDetailsDto(user);
    }

    public void handleForgotPassword(String email) {
        var user = getUserByEmail(email);
        emailService.sendChangePasswordEmail(user);
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
