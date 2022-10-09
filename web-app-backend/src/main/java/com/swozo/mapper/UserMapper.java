package com.swozo.mapper;

import com.swozo.api.web.course.dto.ParticipantDetailsDto;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.persistence.User;
import com.swozo.persistence.UserCourseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    @Mapping(target = "participant", expression = "java(toDto(userCourseData.getUser()))")
    public abstract ParticipantDetailsDto toDto(UserCourseData userCourseData);
}
