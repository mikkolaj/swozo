package com.swozo.webservice.service;

import com.swozo.api.orchestratorclient.ScheduleService;
import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.User;
import com.swozo.dto.course.CourseDetailsDto;
import com.swozo.dto.course.CreateCourseRequest;
import com.swozo.mapper.CourseMapper;
import com.swozo.webservice.exceptions.CourseNotFoundException;
import com.swozo.webservice.repository.ActivityRepository;
import com.swozo.webservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
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

    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public CourseDetailsDto getCourseDetails(Long id) {
        var course = getCourse(id);
        // TODO this will let us check if they are present by refreshing the page MAKE IT BeTtEr
        activityModuleService
                .provideLinksForActivityModules(course.getActivities().stream().flatMap(x -> x.getModules().stream()).toList());

        return courseMapper.toDto(getCourse(id));
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
        Course course = getCourse(id);
        return course.getActivities();
    }

    public Course addStudent(Long courseId, String studentEmail) {
        Course course = getCourse(courseId);
        //TODO check if student has student role
        User student = userService.getUserByEmail(studentEmail);
        course.addStudent(student);
        courseRepository.save(course);
        return course;
    }

    public Course deleteStudent(Long courseId, String studentEmail) {
        Course course = getCourse(courseId);
        //TODO check if student has student role
        User student = userService.getUserByEmail(studentEmail);
        course.deleteStudent(student);
        courseRepository.save(course);
        return course;
    }
}
