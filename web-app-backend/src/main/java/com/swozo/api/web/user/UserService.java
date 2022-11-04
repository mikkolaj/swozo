package com.swozo.api.web.user;

import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.exceptions.types.user.UserNotFoundException;
import com.swozo.api.web.user.dto.UserDetailsDto;
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
    private final UserMapper userMapper;

    public UserDetailsDto getUserInfo(Long userId) {
        return userMapper.toDto(getUserById(userId));
    }

    public User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> UserNotFoundException.of(userId));
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
