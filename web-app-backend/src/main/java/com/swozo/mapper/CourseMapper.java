package com.swozo.mapper;

import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.persistence.Course;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class CourseMapper {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected ActivityMapper activityMapper;

    protected List<ActivityDetailsDto> activitiesToDto(Course course, User user) {
        return course.getActivities().stream()
                .map(activity -> activityMapper.toDto(activity, user))
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target="activities", ignore = true)
    @Mapping(target = "name", source = "createCourseRequest.name")
    @Mapping(target = "teacher", expression = "java(teacher)")
    @Mapping(target = "password", expression = "java(createCourseRequest.password().orElse(null))")
    public abstract Course toPersistence(CreateCourseRequest createCourseRequest, User teacher);

    @Mapping(target = "id", source = "course.id")
    @Mapping(target = "name", source = "course.name")
    @Mapping(target = "lastActivityTime", source = "course.creationTime")
    @Mapping(target = "teacher", expression = "java(userMapper.toDto(course.getTeacher()))")
    @Mapping(target = "students", expression = "java(course.getStudents().stream().map(student -> userMapper.toDto(student)).toList())")
    @Mapping(target = "activities", expression = "java(activitiesToDto(course, user))")
    @Mapping(target = "coursePassword", expression = "java(shouldUsePassword ? course.getPassword() : Optional.empty())")
    public abstract CourseDetailsDto toDto(Course course, User user, boolean shouldUsePassword);

    @Mapping(target = "isPasswordProtected", expression= "java(course.getPassword().isPresent())")
    @Mapping(target = "teacher", expression = "java(userMapper.toDto(course.getTeacher()))")
    public abstract CourseSummaryDto toDto(Course course);
}
