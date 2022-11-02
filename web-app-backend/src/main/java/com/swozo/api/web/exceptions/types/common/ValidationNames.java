package com.swozo.api.web.exceptions.types.common;

public interface ValidationNames {
    interface Fields {
        String ACTIVITIES = "activities";
        String COURSE = "course";
        String END_TIME = "endTime";
        String EMAIL = "email";
        String EXPECTED_STUDENT_COUNT = "expectedStudentCount";
        String NAME = "name";
        String PASSWORD = "password";
        String START_TIME = "startTime";
        String TOKEN = "token";
    }

    interface Errors {
        String MAX = "max";
        String MAX_DURATION = "maxDuration";
        String MIN = "min";
        String MIN_DURATION = "minDuration";
        String MIN_START_TIME = "minStartTime";
        String MIN_TIME_BETWEEN = "minTimeBetween";
        String FORBIDDEN_CHARACTERS = "forbiddenCharacters";
        String PASSWORD_ALLOWED_SPECIALS = "passwordAllowedSpecials";
        String PASSWORD_MIN_DIGITS = "passwordMinDigits";
        String PASSWORD_MIN_LOWERCASE = "passwordMinLowercase";
        String PASSWORD_MIN_UPPERCASE = "passwordMinUppercase";
        String PASSWORD_MIN_SPECIAL = "passwordMinSpecial";
    }
}
