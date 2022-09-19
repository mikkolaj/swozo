package com.swozo.api.web.course;

import com.swozo.api.orchestrator.ScheduleService;
import com.swozo.api.web.activitymodule.ActivityModuleService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.request.AddStudentRequest;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.CourseMapper;
import com.swozo.persistence.Activity;
import com.swozo.persistence.Course;
import com.swozo.persistence.User;
import com.swozo.persistence.UserCourseData;
import com.swozo.security.AccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

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

    public Collection<CourseDetailsDto> getUserCourses(Long userId, RoleDto userRole) {
        var courses = userRole.equals(RoleDto.STUDENT) ?
                courseRepository.getCoursesByStudentsId(userId) :
                courseRepository.getCoursesByTeacherId(userId);

        return courses.stream()
                .map(course -> courseMapper.toDto(course, getCoursePasswordIfAllowed(course, userId)))
                .toList();
    }

    public CourseDetailsDto getCourseDetails(Long courseId, Long userId) {
        var course = courseRepository.getById(courseId);
        // TODO this will let us check if they are present by refreshing the page MAKE IT BeTtEr
        activityModuleService
                .provideLinksForActivityModules(course.getActivities().stream().flatMap(x -> x.getModules().stream()).toList());

        return courseMapper.toDto(course, getCoursePasswordIfAllowed(course, userId));
    }

    @Transactional
    public CourseDetailsDto createCourse(CreateCourseRequest createCourseRequest, Long teacherId) {
        var course = courseMapper.toPersistence(createCourseRequest, teacherId);
        course.setJoinUUID(UUID.randomUUID().toString());

        course.getActivities().forEach(activity -> {
            activity.setCourse(course);
            activity.getModules().forEach(activityModule -> activityModule.setActivity(activity));
        });
        courseRepository.save(course);

        scheduleService.scheduleActivities(course.getActivities());
        return courseMapper.toDto(course, createCourseRequest.password());
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

    public CourseDetailsDto addStudent(Long courseId, AddStudentRequest addStudentRequest) {
        return modifyCourseParticipant(courseId, addStudentRequest.email(), (student, course) -> course.addStudent(student));
    }

    public CourseDetailsDto deleteStudent(Long courseId, String studentEmail) {
        return modifyCourseParticipant(courseId, studentEmail, (student, course) -> course.deleteStudent(student));
    }

    private CourseDetailsDto modifyCourseParticipant(Long courseId, String studentEmail, BiConsumer<User, Course> modifier) {
        var course = courseRepository.getById(courseId);
        //TODO check if student has student role and if teacher is course creator
        var student = userRepository.getByEmail(studentEmail);
        modifier.accept(student, course);
        courseRepository.save(course);
        return courseMapper.toDto(course, course.getPassword());

    }

    private boolean isCreator(Course course, Long userId) {
        return Objects.equals(course.getTeacher().getId(), userId);
    }

    private String getCoursePasswordIfAllowed(Course course, Long userId) {
        return isCreator(course, userId) ? course.getPassword() : null;
    }
}
