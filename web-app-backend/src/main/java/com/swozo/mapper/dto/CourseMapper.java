package com.swozo.mapper.dto;

import com.swozo.databasemodel.Course;
import com.swozo.dto.course.CourseDetailsReq;
import com.swozo.dto.course.CourseDetailsResp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class CourseMapper {
    @Autowired
    protected UserMapper userMapper;
    @Autowired
    protected ActivityMapper activityMapper;

    // it will copy every field that has the same name in courseDetailsReq to Course,
    // this first @Mapping is equivalent to new Course(..., teacher = userMapper.fromId(teacherId), ...)

    @Mapping(target = "teacher", expression = "java(userMapper.fromId(teacherId))")
    @Mapping(target = "students", expression = "java(courseDetailsReq.studentEmails().stream().map(userMapper::fromEmail).toList())")
    @Mapping(target = "activities", expression = "java(courseDetailsReq.activityDetailReqs().stream().map(activityMapper::toPersistence).toList())")
    public abstract Course toPersistence(CourseDetailsReq courseDetailsReq, long teacherId);

    // if some variable has different name but doesn't require mapping with expression you can use
    // @Mapping (target = "propertyNameInCourse", source = "courseDetailsReq.anotherPropertyName")

    @Mapping(target = "lastActivity", source = "course.creationTime")
    @Mapping(target = "teacher", expression = "java(userMapper.toModel(course.getTeacher()))")
    @Mapping(target = "students", expression = "java(course.getStudents().stream().map(userMapper::toModel).toList())")
    @Mapping(target = "activities", expression = "java(course.getActivities().stream().map(activityMapper::toModel).toList())")
    public abstract CourseDetailsResp toModel(Course course);

    public Course toPersistence(CourseDetailsReq courseDetailsReq, Course oldCourse) {
        var course = toPersistence(courseDetailsReq, oldCourse.getTeacher().getId());
        course.setId(oldCourse.getId());
        return course;
    }
}
