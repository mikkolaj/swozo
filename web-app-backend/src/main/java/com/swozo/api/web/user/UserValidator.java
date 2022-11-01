package com.swozo.api.web.user;

import com.swozo.api.web.exceptions.types.common.ValidationErrors;
import com.swozo.api.web.user.request.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.swozo.util.CommonValidators.allSchemaRequiredFieldsPresent;
import static com.swozo.util.CommonValidators.unique;

@Service
@RequiredArgsConstructor
public class UserValidator {
    private final UserRepository userRepository;

    public void validateCreateUserRequest(CreateUserRequest createUserRequest) {
        ValidationErrors.builder()
                .putEachFailed(allSchemaRequiredFieldsPresent(createUserRequest))
                .putIfFails(unique("email", userRepository.findByEmail(createUserRequest.email())))
                .build()
                .throwIfAnyPresent("Invalid user data");
    }

}
