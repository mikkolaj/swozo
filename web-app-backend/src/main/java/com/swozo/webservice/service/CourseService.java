package com.swozo.webservice.service;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.User;
import com.swozo.webservice.repository.CourseRepository;
import com.swozo.webservice.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public Course getCourse(long id){
        return courseRepository.getById(id);
    }

    public void createCourse(Course newCourse){
        courseRepository.save(newCourse);
    }

    public void deleteCourse(long id){
        courseRepository.deleteById(id);
    }

    public void updateCourse(long id, Course newCourse){
        Course course = courseRepository.getById(id);
        course.setName(newCourse.getName());
        course.setSubject(newCourse.getSubject());
        course.setDescription(newCourse.getDescription());
        course.setCreationTime(newCourse.getCreationTime());
        course.setActivities(newCourse.getActivities());
        course.setStudents(newCourse.getStudents());
//        tu sie zastanawiam czy wgl jest taka możliwośc że update kursu będzie zmieniał w nim
//        pole "teacher" czy jeden kurs nie jest na stae przypisany do nauczyciela?
        courseRepository.save(course);
    }

    public Collection<Activity> courseActivityList(long id){
        Course course = courseRepository.getById(id);
        return course.getActivities();
    }

    public void addSudent(long courseId, long studentId){
        Course course = courseRepository.getById(courseId);
        User student = studentRepository.getById(studentId);
        course.addStudent(student);
    }
}
