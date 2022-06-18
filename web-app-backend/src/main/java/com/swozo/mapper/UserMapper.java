package com.swozo.mapper;

import com.swozo.databasemodel.users.User;
import com.swozo.dto.user.UserDetailsResp;
import com.swozo.repository.UserRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected UserRepository userRepository;

    public User fromEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public User fromId(long userId) {
        return userRepository.findById(userId).orElseThrow();
    }

    // TODO proper mapping
    public UserDetailsResp toModel(User user) {
        return new UserDetailsResp(user.getEmail(), "Grzegorz", "Rogus");
    }
}
