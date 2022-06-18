package com.swozo.webservice.service;

import com.swozo.databasemodel.users.Role;
import com.swozo.databasemodel.users.User;
import com.swozo.repository.UserRepository;
import com.swozo.webservice.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public boolean hasUserRole(Long userId, String roleName) {
        User user = getUserById(userId);

        //sorry but I love this line
        return user.getRoles().stream().map(Role::getName).toList().contains(roleName);
    }
}
