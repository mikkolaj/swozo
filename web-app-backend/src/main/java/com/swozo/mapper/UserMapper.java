package com.swozo.mapper;

import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.persistence.User;
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

    public abstract UserDetailsDto toDto(User user);
}
