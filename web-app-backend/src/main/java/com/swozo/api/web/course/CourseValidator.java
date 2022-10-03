package com.swozo.api.web.course;

import com.swozo.api.web.exceptions.types.common.ValidationError;
import com.swozo.api.web.exceptions.types.common.ValidationErrors;
import com.swozo.api.web.exceptions.types.course.AlreadyAMemberException;
import com.swozo.api.web.exceptions.types.course.NotACreatorException;
import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.course.request.CreateCourseRequest;
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
    private static final Duration minTimeBeforeActivityStart = Duration.ofMinutes(10);
    private static final Duration minTimeBetweenActivities = minTimeBeforeActivityStart;
    private static final int MIN_STUDENT_COUNT = 1;
    private static final int MAX_STUDENT_COUNT = 100;

    public void validateNewCourse(CreateCourseRequest createCourseRequest) {
        var courseErrors = validateCourseData(createCourseRequest);
        var activityErrors = createCourseRequest.activities().stream()
                    .map((activity) -> validateActivityData(activity, createCourseRequest.activities()))
                    .collect(Collectors.toCollection(ArrayList::new));

        ValidationErrors.builder()
            .combineWith(courseErrors, "course")
            .combineWithIndices(activityErrors, "activities")
            .build()
            .throwIfAnyPresent("Invalid course data");
    }

    private ValidationErrors.Builder validateCourseData(CreateCourseRequest createCourseRequest) {
        return ValidationErrors.builder()
                .putEachFailed(allSchemaRequiredFieldsPresent(createCourseRequest))
                .putIfFails(presentAndNotEmpty("name", createCourseRequest.name()))
                .putIfFails(
                        numberInBounds(
                            "expectedStudentCount", createCourseRequest.expectedStudentCount(),
                            MIN_STUDENT_COUNT, MAX_STUDENT_COUNT
                        )
                        .map(error -> error.withArg("min", MIN_STUDENT_COUNT).withArg("max", MAX_STUDENT_COUNT))
                )
                .putIfFails(unique("name", () -> courseRepository.findByName(createCourseRequest.name())));
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
            .putIfFails(presentAndNotEmpty("name", createActivityRequest.name()))
            .putIfFails(unique("name",
                    () -> otherActivities.stream().filter(activity -> createActivityRequest.name().equals(activity.name())).findAny())
            )
            .putIfFails(
                    isInFuture("startTime", createActivityRequest.startTime(), minTimeBeforeActivityStart)
                        .map(error -> error.withArg(
                                "minStartTime", LocalDateTime.now().plus(minTimeBeforeActivityStart))
                        )
            )
            .putIfFails(
                    timeDeltaInBounds(
                    "endTime",
                        createActivityRequest.startTime(),
                        createActivityRequest.endTime(),
                        minActivityDuration,
                        maxActivityDuration
                    )
                    .map(error -> error.withArg("minDuration", minActivityDuration.toMinutes()))
                    .map(error -> error.withArg("maxDuration", maxActivityDuration.toMinutes()))
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
        return unique("name", () ->
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
                                "startTime",
                                activity.startTime(), activity.endTime(),
                                createActivityRequest.startTime(), createActivityRequest.endTime(),
                                minTimeBetweenActivities
                        )
                )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findAny()
                .map(error -> error.withArg("minTimeBetween", minTimeBetweenActivities.toMinutes()));
    }
}
