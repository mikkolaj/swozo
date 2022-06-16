package com.swozo.webservice.service;

import com.swozo.api.auth.dto.AppRole;
import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.Role;
import com.swozo.databasemodel.users.User;
import com.swozo.repository.RoleRepository;
import com.swozo.repository.UserRepository;
import com.swozo.webservice.exceptions.CourseNotFoundException;
import com.swozo.webservice.exceptions.StudentNotFoundException;
import com.swozo.webservice.exceptions.TeacherNotFoundException;
import com.swozo.webservice.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository, RoleRepository roleRepository) {
//        TODO możesz tu dac jakeiś tworzenie kursu i zobaczyć czy sie udaje na bazie...
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        Arrays.stream(AppRole.values())
                .map(AppRole::toString)
                .filter(name -> roleRepository.findByName(name) == null)
                .forEach(name -> roleRepository.save(new Role(name)));
//        tryout
        Course c1 = new Course("kurs1");
        c1.setDescription("opis");
        c1.setSubject("INFORMATYKA");
        Role teacherRole = roleRepository.findByName(AppRole.TEACHER.toString());
        User teacher = new User("e-mail", "haslo", List.of(teacherRole));
        userRepository.save(teacher);

        c1.setTeacher(teacher);
        createCourse(c1);
        long courseId = c1.getId();
        System.out.println("id: " + courseId);
        System.out.println("course: " + c1);
        System.out.println("teacher: " + teacher);

        Course c2 = new Course("kurs1");
        c2.setDescription("opis");
        c2.setSubject("MATEMATYKA");
        c2.setTeacher(teacher);
        updateCourse(courseId, c2);

//        Course getter = getCourse(courseId);
//        System.out.println("getter: " + getter);


//        deleteCourse(courseId);


//        end
    }

    public Collection<Course> getCoursesForTeacher(Long teacherId){
        return courseRepository.getTeacherCourses(teacherId);
    }

    public Course getCourse(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public Course createCourse(Course newCourse) {
//        newCourse.getActivities().forEach(activity -> activity.setCourse(newCourse));
        Long id = newCourse.getTeacher().getId();
        User teacher = userRepository.findById(id)
                .orElseThrow(() -> new TeacherNotFoundException(id));
        newCourse.setTeacher(teacher);
        courseRepository.save(newCourse);
        return newCourse;
    }

    public void deleteCourse(Long id) {
//        TODO sprawdz czy usuwaja się activities...
        courseRepository.deleteById(id);
    }

    public Course updateCourse(Long id, Course newCourse) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        course.setName(newCourse.getName());
        course.setSubject(newCourse.getSubject());
        course.setDescription(newCourse.getDescription());
        course.setCreationTime(newCourse.getCreationTime());
        courseRepository.save(course);
        return course;
    }

    public Collection<Activity> courseActivityList(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
        return course.getActivities();
    }

    public Course addSudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(courseId));
        course.addStudent(student);
        courseRepository.save(course);
        return course;
    }

    public Course deleteStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(courseId));
        course.deleteStudent(student);
        courseRepository.save(course);
        return course;
    }
}
