package com.swozo.security;

import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.PasswordData;
import org.passay.PasswordGenerator;

import java.util.List;

@RequiredArgsConstructor
public class PasswordHandler {
    private final List<CharacterRule> rules;
    private final PasswordGenerator passwordGenerator;

    public String generatePassword(int length) {
        return passwordGenerator.generatePassword(length, rules);
    }

    public void validatePassword(String password) {
        var passwordData = new PasswordData(password);

        rules.stream().forEach(rule -> {
            // TODO
            var res = rule.validate(passwordData);
            System.out.println(res);
        });
    }
}
