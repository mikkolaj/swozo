package com.swozo.api.web.sandbox;

import com.swozo.api.web.course.CourseRepository;
import com.swozo.api.web.exceptions.types.common.ValidationErrors;
import com.swozo.api.web.exceptions.types.common.ValidationNames;
import com.swozo.api.web.exceptions.types.course.MaxSandboxesCountExceededException;
import com.swozo.api.web.mda.policy.PolicyRepository;
import com.swozo.api.web.sandbox.request.CreateSandboxEnvironmentRequest;
import com.swozo.persistence.mda.policies.PolicyType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.swozo.api.web.mda.policy.PolicyService.getPolicyValueOrDefault;
import static com.swozo.util.CommonValidators.numberInBounds;

@Service
@RequiredArgsConstructor
public class SandboxValidator {
    private final static int MIN_SANDBOX_TIME_MINUTES = 5;
    private final static int MAX_RESULTS_VALID_FOR_MINUTES = 30;
    private final CourseRepository courseRepository;
    private final PolicyRepository policyRepository;

    public void validateCreateSandboxRequest(Long creatorId, CreateSandboxEnvironmentRequest request) {
        var policies = policyRepository.getAllNotMdaDependantByTeacherIdMap(creatorId);
        var maxParallelSandboxes = getPolicyValueOrDefault(policies, PolicyType.MAX_PARALLEL_SANDBOXES);
        var maxStudents = getPolicyValueOrDefault(policies, PolicyType.MAX_STUDENTS);
        var maxActivityLength = getPolicyValueOrDefault(policies, PolicyType.MAX_ACTIVITY_DURATION_MINUTES);

        if (courseRepository.countBySandboxModeIsTrueAndTeacherId(creatorId) >= maxParallelSandboxes) {
            throw new MaxSandboxesCountExceededException();
        }

        ValidationErrors.builder()
                .putIfFails(
                        numberInBounds(ValidationNames.Fields.STUDENT_COUNT, request.studentCount(), 0, maxStudents)
                )
                .putIfFails(
                        numberInBounds(
                                ValidationNames.Fields.VALID_FOR_MINUTES,
                                request.validForMinutes(),
                                MIN_SANDBOX_TIME_MINUTES,
                                maxActivityLength
                        )
                )
                .putIfFails(
                        numberInBounds(
                                ValidationNames.Fields.RESULTS_VALID_FOR_MINUTES,
                                request.resultsValidForMinutes(),
                                0,
                                MAX_RESULTS_VALID_FOR_MINUTES
                        )
                )
                .build()
                .throwIfAnyPresent("Invalid sandbox data");
    }
}
