package com.swozo.mapper;

import com.swozo.api.web.course.dto.ParticipantDetailsDto;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.dto.UserDetailsDto;
import com.swozo.persistence.user.User;
import com.swozo.persistence.user.UserCourseData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected UserRepository userRepository;

    public abstract UserDetailsDto toDto(User user);

    @Mapping(target = "participant", expression = "java(toDto(userCourseData.getUser()))")
    public abstract ParticipantDetailsDto toDto(UserCourseData userCourseData);
}
