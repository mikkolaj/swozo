package com.swozo.api.web.course;

import com.swozo.api.orchestrator.ScheduleService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.course.request.JoinCourseRequest;
import com.swozo.api.web.course.request.ModifyParticipantRequest;
import com.swozo.api.web.exceptions.types.course.CourseNotFoundException;
import com.swozo.api.web.exceptions.types.course.InvalidCoursePasswordException;
import com.swozo.api.web.exceptions.types.user.UserNotFoundException;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.CourseMapper;
import com.swozo.persistence.Course;
import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;
    private final ScheduleService scheduleService;
    private final CourseValidator courseValidator;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<CourseDetailsDto> getUserCourses(Long userId, RoleDto userRole) {
        var courses = userRole.equals(RoleDto.STUDENT) ?
                courseRepository.getCoursesByStudentsId(userId) :
                courseRepository.getCoursesByTeacherId(userId);

        return courses.stream()
                .map(course -> courseMapper.toDto(course, course.isCreator(userId)))
                .toList();
    }

    public CourseDetailsDto getCourseDetails(Long courseId, Long userId) {
        var course = courseRepository.getById(courseId);
        return courseMapper.toDto(course, course.isCreator(userId));
    }

    public CourseSummaryDto getCourseSummary(String joinUUID) {
       return courseRepository.getByJoinUUID(joinUUID)
               .map(courseMapper::toDto)
               .orElseThrow(() -> CourseNotFoundException.withUUID(joinUUID));
    }

    @Transactional
    public CourseDetailsDto createCourse(CreateCourseRequest createCourseRequest, Long teacherId, boolean sandboxMode) {
        if (!sandboxMode) {
            courseValidator.validateNewCourse(createCourseRequest);
        }

        var course = courseMapper.toPersistence(createCourseRequest, userService.getUserById(teacherId));
        course.setJoinUUID(UUID.randomUUID().toString());
        course.getActivities().forEach(activity -> {
            activity.setCourse(course);
            activity.getModules().forEach(activityModule -> activityModule.setActivity(activity));
        });
        course.setSandboxMode(sandboxMode);

        courseRepository.save(course);

        scheduleService.scheduleActivities(course.getActivities());
        return courseMapper.toDto(course, true);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
        // TODO: remove all files
    }

    public Course updateCourse(Long id, Long teacherId, CreateCourseRequest createCourseRequest) {
        // TODO validate that teacherId = id of creator etc...
        var oldCourse = courseRepository.getById(id);
        var course = courseMapper.toPersistence(createCourseRequest, oldCourse.getTeacher());
        course.setId(oldCourse.getId());

        // TODO maybe check what should be overwritten
        courseRepository.save(course);
        return course;
    }

    @Transactional
    public CourseDetailsDto joinCourse(JoinCourseRequest joinCourseRequest, Long userId) {
        var course = courseRepository.getByJoinUUID(joinCourseRequest.joinUUID())
                .orElseThrow(() -> CourseNotFoundException.withUUID(joinCourseRequest.joinUUID()));
        var student = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::ofAuthenticationOwner);

        courseValidator.validateJoinCourseRequest(student, course);

        if (!course.getPassword().equals(joinCourseRequest.password())) {
            throw new InvalidCoursePasswordException();
        }

        course.addStudent(student);
        courseRepository.save(course);
        return courseMapper.toDto(course, false);
    }

    @Transactional
    public CourseDetailsDto addStudent(Long teacherId, Long courseId, ModifyParticipantRequest modifyParticipantRequest) {
        return modifyCourseParticipant(courseId, modifyParticipantRequest.email(), (student, course) -> {
            courseValidator.validateAddStudentRequest(student, teacherId, course);
            course.addStudent(student);
        });
    }

    @Transactional
    public CourseDetailsDto deleteStudent(Long teacherId, Long courseId, String studentEmail) {
        return modifyCourseParticipant(courseId, studentEmail, (student, course) -> course.deleteStudent(student));
    }

    private CourseDetailsDto modifyCourseParticipant(Long courseId, String studentEmail, BiConsumer<User, Course> modifier) {
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> CourseNotFoundException.withId(courseId));
        var student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> UserNotFoundException.withEmail(studentEmail));

        modifier.accept(student, course);
        courseRepository.save(course);
        return courseMapper.toDto(course, true);
    }
}
