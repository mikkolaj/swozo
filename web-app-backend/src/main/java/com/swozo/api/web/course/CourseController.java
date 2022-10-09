package com.swozo.api.web.course;

import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.course.request.AddStudentRequest;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.course.request.JoinCourseRequest;
import com.swozo.persistence.Activity;
import com.swozo.persistence.Course;
import com.swozo.persistence.User;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/courses")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class CourseController {
    private final Logger logger = LoggerFactory.getLogger(CourseController.class);
    private final CourseService courseService;
    private final AuthService authService;

    @GetMapping("/all-system-courses")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<Course> getAllSystemCourses(AccessToken token) {
        return courseService.getAllCourses();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public Collection<CourseDetailsDto> getUserCourses(AccessToken token) {
        var userId = token.getUserId();
        logger.info("course list for user with id: {}", userId);
        return courseService.getUserCourses(userId, authService.oneOf(token, RoleDto.TEACHER, RoleDto.STUDENT));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public CourseDetailsDto getCourse(AccessToken token, @PathVariable Long id) {
        logger.info("course info getter for id: {}", id);
        return courseService.getCourseDetails(id, token.getUserId());
    }

    @GetMapping("/summary/{uuid}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public CourseSummaryDto getPublicCourseData(AccessToken token, @PathVariable String uuid) {
        return courseService.getCourseSummary(uuid);
    }

    @PatchMapping("/join")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public CourseDetailsDto joinCourse(AccessToken token, @RequestBody JoinCourseRequest joinCourseRequest) {
        return courseService.joinCourse(joinCourseRequest, token.getUserId());
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto addCourse(AccessToken token, @RequestBody CreateCourseRequest createCourseRequest) {
        logger.info("creating new course with name: {}", createCourseRequest.name());
        return courseService.createCourse(createCourseRequest, token.getUserId());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteCourse(AccessToken token, @PathVariable Long id) {
        logger.info("deleting course with id: {}", id);
        courseService.deleteCourse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course editCourse(AccessToken token, @PathVariable Long id, @RequestBody CreateCourseRequest createCourseRequest) {
        logger.info("editing course with id: {}", id);
        return courseService.updateCourse(id, token.getUserId(), createCourseRequest);
    }

    @GetMapping("/{id}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<Activity> getCourseActivityList(AccessToken token, @PathVariable Long id) {
        // TODO not sure if this endpoint is needed since we return this data in getCourse
        logger.info("activity list from course with id: {}", id);
        return courseService.courseActivityList(id);
    }

    @PostMapping("/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto addStudentToCourse(AccessToken token, @PathVariable Long courseId, @RequestBody AddStudentRequest addStudentRequest) {
        logger.info("adding student with email: {} to course with id: {}", addStudentRequest.email(), courseId);
        return courseService.addStudent(token.getUserId(), courseId, addStudentRequest);
    }

    @DeleteMapping("/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto removeStudentFromCourse(AccessToken token, @PathVariable Long courseId, @RequestBody User student) {
        // TODO email - not User in RequestBody
        logger.info("removing student with email: {} from course with id: {}", student.getEmail(), courseId);
        return courseService.deleteStudent(token.getUserId(), courseId, student.getEmail());
    }

}
