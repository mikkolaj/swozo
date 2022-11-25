package com.swozo.api.web.course;

import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.course.request.EditCourseRequest;
import com.swozo.api.web.exceptions.types.common.ValidationError;
import com.swozo.api.web.exceptions.types.common.ValidationErrors;
import com.swozo.api.web.exceptions.types.common.ValidationNames;
import com.swozo.api.web.exceptions.types.course.AlreadyAMemberException;
import com.swozo.api.web.exceptions.types.course.NotACreatorException;
import com.swozo.api.web.mda.policy.PolicyRepository;
import com.swozo.mapper.ActivityMapper;
import com.swozo.persistence.Course;
import com.swozo.persistence.mda.policies.Policy;
import com.swozo.persistence.mda.policies.PolicyType;
import com.swozo.persistence.user.User;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.swozo.api.web.mda.policy.PolicyService.getPolicyValueOrDefault;
import static com.swozo.util.CommonValidators.*;

@Service
@RequiredArgsConstructor
public class CourseValidator {
    private final CourseRepository courseRepository;
    private final ActivityMapper activityMapper;
    private final PolicyRepository policyRepository;

    private static final Duration minActivityDuration = Duration.ofMinutes(1);
    private static final Duration minTimeBeforeActivityStart = Duration.ofMinutes(0);
    private static final Duration minTimeBetweenActivities = minTimeBeforeActivityStart;
    private static final int MIN_STUDENT_COUNT = 1;

    public void validateNewCourse(CreateCourseRequest createCourseRequest, Long teacherId) {
        var teacherPolicies = policyRepository.getAllNotMdaDependantByTeacherIdMap(teacherId);
        var courseErrors = validateCourseData(teacherPolicies, createCourseRequest);
        var activityErrors = createCourseRequest.activities().stream()
                    .map((activity) -> validateActivityData(teacherPolicies, activity, createCourseRequest.activities()))
                    .collect(Collectors.toCollection(ArrayList::new));

        ValidationErrors.builder()
            .combineWith(courseErrors, ValidationNames.Fields.COURSE)
            .combineWithIndices(activityErrors, ValidationNames.Fields.ACTIVITIES)
            .build()
            .throwIfAnyPresent("Invalid course data");
    }

    private ValidationErrors.Builder validateCourseData(
            Map<PolicyType, Policy> teacherPolicies,
            CreateCourseRequest createCourseRequest
    ) {
        var maxStudents = getPolicyValueOrDefault(teacherPolicies, PolicyType.MAX_STUDENTS);

        return ValidationErrors.builder()
                .putEachFailed(allSchemaRequiredFieldsPresent(createCourseRequest))
                .putIfFails(presentAndNotEmpty(ValidationNames.Fields.NAME, createCourseRequest.name()))
                .putIfFails(
                        numberInBounds(
                            ValidationNames.Fields.EXPECTED_STUDENT_COUNT, createCourseRequest.expectedStudentCount(),
                            MIN_STUDENT_COUNT, maxStudents
                        )
                )
                .putIfFails(unique(ValidationNames.Fields.NAME, courseRepository.findByName(createCourseRequest.name())));
    }

    private ValidationErrors.Builder validateActivityData(
            Map<PolicyType, Policy> teacherPolicies,
            CreateActivityRequest createActivityRequest,
            List<CreateActivityRequest> allActivities
    ) {
        var otherActivities = allActivities.stream()
                .filter(activity -> !activity.equals(createActivityRequest))
                .toList();

        var maxActivityDuration = getPolicyValueOrDefault(teacherPolicies, PolicyType.MAX_ACTIVITY_DURATION_MINUTES);

        return ValidationErrors.builder()
            .putEachFailed(allSchemaRequiredFieldsPresent(createActivityRequest))
            .putIfFails(isAUniqueActivity(createActivityRequest, allActivities))
            .putIfFails(presentAndNotEmpty(ValidationNames.Fields.NAME, createActivityRequest.name()))
            .putIfFails(unique(ValidationNames.Fields.NAME,
                     otherActivities.stream().filter(activity -> createActivityRequest.name().equals(activity.name())).findAny())
            )
            .putIfFails(
                    isInFuture(ValidationNames.Fields.START_TIME, createActivityRequest.startTime(), minTimeBeforeActivityStart)
                        .map(error -> error.withArg(
                                ValidationNames.Errors.MIN_START_TIME, LocalDateTime.now().plus(minTimeBeforeActivityStart))
                        )
            )
            .putIfFails(
                    timeDeltaInBounds(
                        ValidationNames.Fields.END_TIME,
                        createActivityRequest.startTime(),
                        createActivityRequest.endTime(),
                        minActivityDuration,
                        Duration.ofMinutes(maxActivityDuration)
                    )
                    .map(error -> error
                            .withArg(ValidationNames.Errors.MIN_DURATION, minActivityDuration.toMinutes())
                            .withArg(ValidationNames.Errors.MAX_DURATION, maxActivityDuration)
                    )
            )
            .putIfFails(notOverlappingWithOtherActivities(createActivityRequest, otherActivities));
    }

    public void validateAddStudentRequest(User student, Long teacherId, Course course) {
        validateCreatorAndNotSandbox(course, teacherId);
        validateJoinCourseRequest(student, course);
    }

    public void validateJoinCourseRequest(User student, Course course) {
        validateNotSandbox(course);
        if (course.getStudents().stream().anyMatch(x -> x.getUser().getId().equals(student.getId()))) {
            throw new AlreadyAMemberException(String.format(
                    "%s already belongs to the course: %s", student.getEmail(), course.getName()
            ));
        }
    }

    public void validateEditCourseRequest(Course course, EditCourseRequest request, Long editorId) {
        validateCreatorAndNotSandbox(course, editorId);
        ValidationErrors.builder()
            .putIfFails(request.name().flatMap(name ->
                    unique(ValidationNames.Fields.NAME, courseRepository.findByName(name)))
            )
            .build()
            .throwIfAnyPresent("Invalid course data");
    }

    public void validateAddActivityRequest(Course course, CreateActivityRequest request, Long editorId) {
        validateCreatorAndNotSandbox(course, editorId);
        var teacherPolicies = policyRepository.getAllNotMdaDependantByTeacherIdMap(editorId);

        var alreadyPresentActivities = course.getActivities().stream()
                .map(activityMapper::toRequest)
                .toList();

        validateActivityData(teacherPolicies, request, alreadyPresentActivities)
                .build()
                .throwIfAnyPresent("Invalid activity data");
    }

    public void validateNotSandbox(Course course) {
        if (course.isSandbox()) {
            throw new UnauthorizedException("Cant edit sandbox course");
        }
    }

    public void validateCreatorAndNotSandbox(Course course, Long userId) {
        validateNotSandbox(course);
        if (!course.isCreator(userId)) {
            throw new NotACreatorException("Only course creator can edit a course");
        }
    }

    private Optional<ValidationError> isAUniqueActivity(
            CreateActivityRequest createActivityRequest,
            List<CreateActivityRequest> allActivities
    ) {
        return unique(ValidationNames.Fields.NAME,
                allActivities.stream().filter(activity -> activity.equals(createActivityRequest)).count() > 1 ?
                        Optional.of(createActivityRequest) : Optional.empty()
        );
    }

    private Optional<ValidationError> notOverlappingWithOtherActivities(
            CreateActivityRequest createActivityRequest,
            List<CreateActivityRequest> otherActivities
    ) {
        return otherActivities.stream()
                .map(activity -> notOverlapping(
                                ValidationNames.Fields.START_TIME,
                                activity.startTime(), activity.endTime(),
                                createActivityRequest.startTime(), createActivityRequest.endTime(),
                                minTimeBetweenActivities
                        )
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .map(error -> error.withArg(ValidationNames.Errors.MIN_TIME_BETWEEN, minTimeBetweenActivities.toMinutes()));
    }
}
