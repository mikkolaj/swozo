package com.swozo.webservice.service;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
import com.swozo.webservice.exceptions.CourseNotFoundException;
import com.swozo.webservice.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedHashSet;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserService userService;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    public Collection<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Collection<Course> getUserCourses(Long userId) {
        if (userService.hasUserRole(userId, "STUDENT")) {
            return courseRepository.getCoursesByStudentsId(userId);
        } else {
            return courseRepository.getCoursesByTeacherId(userId);
        }
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public Course createCourse(Course newCourse, Long teacherId) {
        User teacher = userService.getUserById(teacherId);
        newCourse.setTeacher(teacher);

        Collection<User> students = new LinkedHashSet<>();
        for (User student : newCourse.getStudents()) {
            students.add(userService.getUserByEmail(student.getEmail()));
        }
        newCourse.setStudents(students);

        for (Activity activity : newCourse.getActivities()) {
            activity.setCourse(newCourse);
        }

        courseRepository.save(newCourse);
        return newCourse;
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Long id, Course newCourse) {
        Course course = getCourse(id);
        course.setName(newCourse.getName());
        course.setSubject(newCourse.getSubject());
        course.setDescription(newCourse.getDescription());
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
