package com.swozo.webservice.service;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
import com.swozo.repository.UserRepository;
import com.swozo.webservice.exceptions.CourseNotFoundException;
import com.swozo.webservice.exceptions.StudentNotFoundException;
import com.swozo.webservice.exceptions.TeacherNotFoundException;
import com.swozo.webservice.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Collection<Course> getCoursesForTeacher(Long teacherId) {
        return courseRepository.getTeacherCourses(teacherId);
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public Course createCourse(Course newCourse) {
        Long id = newCourse.getTeacher().getId();
        User teacher = userRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));
        newCourse.setTeacher(teacher);
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

    public Course addSudent(Long courseId, Long studentId) {
        Course course = getCourse(courseId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(courseId));
        course.addStudent(student);
        courseRepository.save(course);
        return course;
    }

    public Course deleteStudent(Long courseId, Long studentId) {
        Course course = getCourse(courseId);
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(courseId));
        course.deleteStudent(student);
        courseRepository.save(course);
        return course;
    }
}
