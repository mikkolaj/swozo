package com.swozo.api.web.sandbox;

import com.swozo.api.web.course.CourseRepository;
import com.swozo.api.web.sandbox.request.CreateSandboxEnvironmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SandboxValidator {
    private final CourseRepository courseRepository;

    public void validateCreateSandboxRequest(Long creatorId, CreateSandboxEnvironmentRequest request) {
        courseRepository.findBySandboxModeIsTrueAndTeacherId(creatorId)
                .ifPresent(course -> {
                    // TODO proper error
                    throw new RuntimeException("At most 1 sandbox allowed at a time");
                });

    }
}
