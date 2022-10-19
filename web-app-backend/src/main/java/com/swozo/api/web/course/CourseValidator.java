package com.swozo.api.web.course;

import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.exceptions.types.common.ValidationError;
import com.swozo.api.web.exceptions.types.common.ValidationErrors;
import com.swozo.api.web.exceptions.types.common.ValidationNames;
import com.swozo.api.web.exceptions.types.course.AlreadyAMemberException;
import com.swozo.api.web.exceptions.types.course.NotACreatorException;
import com.swozo.persistence.Course;
import com.swozo.persistence.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.swozo.util.CommonValidators.*;

@Service
@RequiredArgsConstructor
public class CourseValidator {
    private final CourseRepository courseRepository;

    // TODO: policy based?
    private static final Duration minActivityDuration = Duration.ofMinutes(1);
    private static final Duration maxActivityDuration = Duration.ofMinutes(180);
    private static final Duration minTimeBeforeActivityStart = Duration.ofMinutes(0);
    private static final Duration minTimeBetweenActivities = minTimeBeforeActivityStart;
    private static final int MIN_STUDENT_COUNT = 1;
    private static final int MAX_STUDENT_COUNT = 100;

    public void validateNewCourse(CreateCourseRequest createCourseRequest) {
        var courseErrors = validateCourseData(createCourseRequest);
        var activityErrors = createCourseRequest.activities().stream()
                    .map((activity) -> validateActivityData(activity, createCourseRequest.activities()))
                    .collect(Collectors.toCollection(ArrayList::new));

        ValidationErrors.builder()
            .combineWith(courseErrors, ValidationNames.Fields.COURSE)
            .combineWithIndices(activityErrors, ValidationNames.Fields.ACTIVITIES)
            .build()
            .throwIfAnyPresent("Invalid course data");
    }

    private ValidationErrors.Builder validateCourseData(CreateCourseRequest createCourseRequest) {
        return ValidationErrors.builder()
                .putEachFailed(allSchemaRequiredFieldsPresent(createCourseRequest))
                .putIfFails(presentAndNotEmpty(ValidationNames.Fields.NAME, createCourseRequest.name()))
                .putIfFails(
                        numberInBounds(
                            ValidationNames.Fields.EXPECTED_STUDENT_COUNT, createCourseRequest.expectedStudentCount(),
                            MIN_STUDENT_COUNT, MAX_STUDENT_COUNT
                        )
                        .map(error -> error
                                .withArg(ValidationNames.Errors.MIN, MIN_STUDENT_COUNT)
                                .withArg(ValidationNames.Errors.MAX, MAX_STUDENT_COUNT)
                        )
                )
                .putIfFails(unique(ValidationNames.Fields.NAME, courseRepository.findByName(createCourseRequest.name())));
    }

    private ValidationErrors.Builder validateActivityData(CreateActivityRequest createActivityRequest, List<CreateActivityRequest> allActivities) {
        // TODO: this could be optimised if necessary, for now its ok because these numbers will be in order of 10
        var otherActivities = allActivities.stream()
                .filter(activity -> !activity.equals(createActivityRequest))
                .toList();

        // TODO: day limits(?), valid modules, etc...
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
                        maxActivityDuration
                    )
                    .map(error -> error
                            .withArg(ValidationNames.Errors.MIN_DURATION, minActivityDuration.toMinutes())
                            .withArg(ValidationNames.Errors.MAX_DURATION, maxActivityDuration.toMinutes())
                    )
            )
            .putIfFails(notOverlappingWithOtherActivities(createActivityRequest, otherActivities));
    }

    public void validateAddStudentRequest(User student, Long teacherId, Course course) {
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new NotACreatorException("Only course creator can add a student");
        }
        validateJoinCourseRequest(student, course);
    }

    public void validateJoinCourseRequest(User student, Course course) {
        if (course.getStudents().stream().anyMatch(x -> x.getUser().getId().equals(student.getId()))) {
            throw new AlreadyAMemberException(String.format(
                    "%s already belongs to the course: %s", student.getEmail(), course.getName()
            ));
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
