package com.swozo.api.web.exceptions.types.common;

public interface ValidationNames {
    interface Fields {
        String ACTIVITIES = "activities";
        String COURSE = "course";
        String END_TIME = "endTime";
        String EXPECTED_STUDENT_COUNT = "expectedStudentCount";
        String NAME = "name";
        String START_TIME = "startTime";
    }

    interface Errors {
        String MAX = "max";
        String MAX_DURATION = "maxDuration";
        String MIN = "min";
        String MIN_DURATION = "minDuration";
        String MIN_START_TIME = "minStartTime";
        String MIN_TIME_BETWEEN = "minTimeBetween";
    }
}
