package com.swozo.webservice.service;

import com.swozo.databasemodel.Role;
import com.swozo.databasemodel.User;
import com.swozo.dto.user.UserDetailsResp;
import com.swozo.mapper.UserMapper;
import com.swozo.webservice.exceptions.UserNotFoundException;
import com.swozo.webservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public UserDetailsResp getUserInfo(Long userId) {
        return userMapper.toModel(getUserById(userId));
    }

    public boolean hasUserRole(Long userId, String roleName) {
        User user = getUserById(userId);

        return user.getRoles().stream().map(Role::getName).toList().contains(roleName);
    }
}
