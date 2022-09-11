package com.swozo.api.web.course;

import com.swozo.api.orchestrator.ScheduleService;
import com.swozo.api.web.activitymodule.ActivityModuleService;
import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.CourseMapper;
import com.swozo.persistence.Activity;
import com.swozo.persistence.Course;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;
    private final ScheduleService scheduleService;
    private final ActivityModuleService activityModuleService;

    public Collection<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Collection<CourseDetailsDto> getUserCourses(Long userId) {
        var courses = userService.hasUserRole(userId, "STUDENT") ?
                courseRepository.getCoursesByStudentsId(userId) :
                courseRepository.getCoursesByTeacherId(userId);

        return courses.stream().map(courseMapper::toDto).toList();
    }

    public CourseDetailsDto getCourseDetails(Long id) {
        var course = courseRepository.getById(id);
        // TODO this will let us check if they are present by refreshing the page MAKE IT BeTtEr
        activityModuleService
                .provideLinksForActivityModules(course.getActivities().stream().flatMap(x -> x.getModules().stream()).toList());

        return courseMapper.toDto(course);
    }

    public CourseDetailsDto createCourse(CreateCourseRequest createCourseRequest, Long teacherId) {
        var course = courseMapper.toPersistence(createCourseRequest, teacherId);
        course.getActivities().forEach(activity -> {
            activity.setCourse(course);
            activity.getModules().forEach(activityModule -> activityModule.setActivity(activity));
        });
        courseRepository.save(course);

        scheduleService.scheduleActivities(course.getActivities());
        return courseMapper.toDto(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Long id, Long teacherId, CreateCourseRequest createCourseRequest) {
        // TODO validate that teacherId = id of creator etc...
        var oldCourse = courseRepository.getById(id);
        var course = courseMapper.toPersistence(createCourseRequest, oldCourse.getTeacher().getId());
        course.setId(oldCourse.getId());

        // TODO maybe check what should be overwritten
        courseRepository.save(course);
        return course;
    }

    public Collection<Activity> courseActivityList(Long id) {
        Course course = courseRepository.getById(id);
        return course.getActivities();
    }

    public Course addStudent(Long courseId, String studentEmail) {
        var course = courseRepository.getById(courseId);
        //TODO check if student has student role
        var student = userRepository.getByEmail(studentEmail);
        course.addStudent(student);
        courseRepository.save(course);
        return course;
    }

    public Course deleteStudent(Long courseId, String studentEmail) {
        var course = courseRepository.getById(courseId);
        //TODO check if student has student role
        var student = userRepository.getByEmail(studentEmail);
        course.deleteStudent(student);
        courseRepository.save(course);
        return course;
    }
}
