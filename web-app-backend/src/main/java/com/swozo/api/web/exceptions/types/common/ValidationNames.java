package com.swozo.api.web.exceptions.types.common;

public interface ValidationNames {
    interface Fields {
        String ACTIVITIES = "activities";
        String BASE_VCPU = "baseVcpu";
        String BASE_RAM = "baseRam";
        String BASE_DISK = "baseDisk";
        String BASE_BANDWIDTH = "baseBandwidth";
        String COURSE = "course";
        String DYNAMIC_FIELDS = "__dynamicFields__";
        String EMAIL = "email";
        String END_TIME = "endTime";
        String EXPECTED_STUDENT_COUNT = "expectedStudentCount";
        String ISOLATION_MODE = "isIsolated";
        String MDA_VALUES = "mdaValues";
        String MODULE_VALUES = "moduleValues";
        String NAME = "name";
        String PASSWORD = "password";
        String START_TIME = "startTime";
        String STUDENT_COUNT = "studentCount";
        String TOKEN = "token";
        String SHARED_SERVICE_MODULE_MDA_DTO = "sharedServiceModuleMdaDto";
        String USERS_PER_ADDITIONAL_CORE = "usersPerAdditionalCore";
        String USERS_PER_ADDITIONAL_RAM_GB = "usersPerAdditionalRamGb";
        String USERS_PER_ADDITIONAL_DISK_GB = "usersPerAdditionalDiskGb";
        String USERS_PER_ADDITIONAL_BANDWIDTH_GBS = "usersPerAdditionalBandwidthGbps";
        String VALID_FOR_MINUTES = "validForMinutes";
        String RESULTS_VALID_FOR_MINUTES = "resultsValidForMinutes";
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
