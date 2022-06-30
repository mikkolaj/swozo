package com.swozo.mapper;

import com.swozo.databasemodel.User;
import com.swozo.dto.user.UserDetailsResp;
import com.swozo.webservice.repository.UserRepository;
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

    public abstract UserDetailsResp toModel(User user);
}
