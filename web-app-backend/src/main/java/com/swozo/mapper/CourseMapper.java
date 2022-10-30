package com.swozo.mapper;

import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.persistence.Course;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class CourseMapper {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected ActivityMapper activityMapper;

    @Mapping(target = "id", ignore = true)
    @Mapping(target="activities", ignore = true)
    @Mapping(target = "name", source = "createCourseRequest.name")
    @Mapping(target = "teacher", expression = "java(teacher)")
    @Mapping(target = "password", expression = "java(createCourseRequest.password().orElse(null))")
    public abstract Course toPersistence(CreateCourseRequest createCourseRequest, User teacher);

    @Mapping(target = "lastActivityTime", source = "course.creationTime")
    @Mapping(target = "teacher", expression = "java(userMapper.toDto(course.getTeacher()))")
    @Mapping(target = "students", expression = "java(course.getStudents().stream().map(user -> userMapper.toDto(user)).toList())")
    @Mapping(target = "activities", expression = "java(course.getActivities().stream().map(activityMapper::toDto).toList())")
    @Mapping(target = "coursePassword", expression = "java(shouldUsePassword ? course.getPassword() : Optional.empty())")
    public abstract CourseDetailsDto toDto(Course course, boolean shouldUsePassword);

    @Mapping(target = "isPasswordProtected", expression= "java(course.getPassword().isPresent())")
    public abstract CourseSummaryDto toDto(Course course);
}
