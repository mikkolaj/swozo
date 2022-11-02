package com.swozo.security;

import com.swozo.api.web.exceptions.types.common.ValidationErrorType;
import com.swozo.api.web.exceptions.types.common.ValidationErrors;
import com.swozo.api.web.exceptions.types.common.ValidationNames;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class PasswordHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<CharacterRule> rules;
    private final PasswordGenerator passwordGenerator;
    private final Map<String, Object> validationFailedArgs;

    public String generatePassword(int length) {
        return passwordGenerator.generatePassword(length, rules);
    }

    public String generateStrongRandomToken(int length) {
        return passwordGenerator.generatePassword(length, rules);
    }

    public ValidationErrors.Builder validatePassword(String password) {
        var passwordData = new PasswordData(password);

        return ValidationErrors.builder()
                .putIfFails(
                    rules.stream()
                            .map(rule -> rule.validate(passwordData))
                            .filter(ruleResult -> !ruleResult.isValid())
                            .peek(rule -> logger.debug("Password rule failed {}", rule))
                            .findAny()
                            .map(ruleResult -> ValidationErrorType.INVALID_PASSWORD.forField(ValidationNames.Fields.PASSWORD))
                            .map(validationError -> validationError.withArgs(validationFailedArgs))
                );
    }
}
