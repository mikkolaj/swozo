package com.swozo.webservice.service;

import com.swozo.api.orchestratorclient.ScheduleService;
import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
import com.swozo.dto.course.CourseDetailsReq;
import com.swozo.dto.course.CourseDetailsResp;
import com.swozo.mapper.dto.CourseMapper;
import com.swozo.webservice.exceptions.CourseNotFoundException;
import com.swozo.webservice.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;
    private final ScheduleService scheduleService;

    public Collection<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Collection<CourseDetailsResp> getUserCourses(Long userId) {
        var courses = userService.hasUserRole(userId, "STUDENT") ?
                courseRepository.getCoursesByStudentsId(userId) :
                courseRepository.getCoursesByTeacherId(userId);

        return courses.stream().map(courseMapper::toModel).toList();
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public CourseDetailsResp getCourseDetails(Long id) {
        return courseMapper.toModel(getCourse(id));
    }

    public CourseDetailsResp createCourse(CourseDetailsReq courseDetailsReq, Long teacherId) {
        var course = courseMapper.toPersistence(courseDetailsReq, teacherId);

        for (Activity activity : course.getActivities()) {
            activity.addActivityModule(new ActivityModule());
        }

        scheduleService.scheduleActivities(course.getActivities());
        courseRepository.save(course);
        return courseMapper.toModel(course);
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Long id, Long teacherId, CourseDetailsReq courseDetailsReq) {
        // TODO validate that teacherId = id of creator etc...
        var oldCourse = courseRepository.getById(id);
        var course = courseMapper.toPersistence(courseDetailsReq, oldCourse.getTeacher().getId());
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
