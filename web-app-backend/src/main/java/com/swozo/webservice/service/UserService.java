package com.swozo.webservice.service;

import com.swozo.databasemodel.users.Role;
import com.swozo.databasemodel.users.User;
import com.swozo.dto.user.UserDetailsResp;
import com.swozo.mapper.dto.UserMapper;
import com.swozo.repository.UserRepository;
import com.swozo.webservice.exceptions.UserNotFoundException;
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

    public UserDetailsResp getUserInfo(Long userId){
        return userMapper.toModel(getUserById(userId));
    }

    public boolean hasUserRole(Long userId, String roleName) {
        User user = getUserById(userId);

        //sorry but I love this line
        return user.getRoles().stream().map(Role::getName).toList().contains(roleName);
    }
}
