package com.swozo.mapper;

import com.swozo.databasemodel.Course;
import com.swozo.dto.course.CourseDetailsDto;
import com.swozo.dto.course.CreateCourseRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class CourseMapper {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected ActivityMapper activityMapper;

    @Mapping(target = "teacher", expression = "java(userMapper.fromId(teacherId))")
    @Mapping(target = "students", expression = "java(courseDetailsReq.studentEmails().stream().map(userMapper::fromEmail).toList())")
    @Mapping(target = "activities", expression = "java(courseDetailsReq.activityDetails().stream().map(activityMapper::toPersistence).toList())")
    public abstract Course toPersistence(CreateCourseRequest createCourseRequest, long teacherId);

    @Mapping(target = "lastActivity", source = "course.creationTime")
    @Mapping(target = "teacher", expression = "java(userMapper.toModel(course.getTeacher()))")
    @Mapping(target = "students", expression = "java(course.getStudents().stream().map(userMapper::toModel).toList())")
    @Mapping(target = "activities", expression = "java(course.getActivities().stream().map(activityMapper::toModel).toList())")
    public abstract CourseDetailsDto toDto(Course course);
}
