package com.swozo.api.web.course;

import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.dto.CourseSummaryDto;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.course.request.EditCourseRequest;
import com.swozo.api.web.course.request.JoinCourseRequest;
import com.swozo.api.web.course.request.ModifyParticipantRequest;
import com.swozo.persistence.Course;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

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
        return courseService.getUserCoursesDetails(userId, authService.oneOf(token, RoleDto.TEACHER, RoleDto.STUDENT));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public CourseDetailsDto getCourse(AccessToken token, @PathVariable Long id) {
        logger.info("course info getter for id: {}", id);
        return courseService.getCourseDetails(id, token.getUserId());
    }

    @GetMapping("/summary")
    public List<CourseSummaryDto> getPublicCourses(
            AccessToken token,
            @RequestParam(defaultValue = "0") Long offset,
            @RequestParam(defaultValue = "100") Long limit
    ) {
        return courseService.getPublicCoursesNotParticipatedBy(token.getUserId(), offset, limit);
    }

    @GetMapping("/summary/{uuid}")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public CourseSummaryDto getPublicCourseData(AccessToken token, @PathVariable String uuid) {
        return courseService.getCourseSummary(uuid);
    }

    @PutMapping("/join")
    @PreAuthorize("hasAnyRole('STUDENT')")
    public CourseDetailsDto joinCourse(AccessToken token, @RequestBody JoinCourseRequest joinCourseRequest) {
        return courseService.joinCourse(joinCourseRequest, token.getUserId());
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto addCourse(AccessToken token, @RequestBody CreateCourseRequest createCourseRequest) {
        logger.info("creating new course with name: {}", createCourseRequest.name());
        return courseService.createCourse(createCourseRequest, token.getUserId(), false);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteCourse(AccessToken token, @PathVariable Long id) {
        logger.info("deleting course with id: {}", id);
        courseService.deleteCourse(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto editCourse(AccessToken token, @PathVariable Long id, @RequestBody EditCourseRequest editCourseRequest) {
        logger.info("editing course with id: {}", id);
        return courseService.editCourse(token.getUserId(), id, editCourseRequest);
    }

    @PutMapping("/{id}/activities")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto addSingleActivity(AccessToken token, @PathVariable Long id, @RequestBody CreateActivityRequest createActivityRequest) {
        logger.info("editing course with id: {}", id);
        return courseService.addSingleActivity(token.getUserId(), id, createActivityRequest);
    }

    @DeleteMapping("/{courseId}/activities/{activityId}")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto deleteActivity(AccessToken accessToken, @PathVariable Long courseId, @PathVariable Long activityId) {
        logger.info("deleting activity with id: {}", activityId);
        return courseService.deleteActivity(accessToken.getUserId(), courseId, activityId);
    }

    @PutMapping("/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto addStudentToCourse(AccessToken token, @PathVariable Long courseId, @RequestBody ModifyParticipantRequest student) {
        logger.info("adding student with email: {} to course with id: {}", student.email(), courseId);
        return courseService.addStudent(token.getUserId(), courseId, student);
    }

    @DeleteMapping("/{courseId}/students")
    @PreAuthorize("hasRole('TEACHER')")
    public CourseDetailsDto removeStudentFromCourse(AccessToken token, @PathVariable Long courseId, @RequestBody ModifyParticipantRequest student) {
        logger.info("removing student with email: {} from course with id: {}", student.email(), courseId);
        return courseService.deleteStudent(token.getUserId(), courseId, student.email());
    }
}
