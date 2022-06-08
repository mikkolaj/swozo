package com.swozo.jsonparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swozo.model.scheduling.ScheduleRequest;

public class ModelToJsonParser {
    public static String mapScheduleRequestToJson(ScheduleRequest scheduleRequest) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            return mapper
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(scheduleRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "";
        }
    }
}
