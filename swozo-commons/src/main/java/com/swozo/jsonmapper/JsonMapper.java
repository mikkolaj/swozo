package com.swozo.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class JsonMapper {
    private static final Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    private static Optional<String> parseObject(Object targetObject, ObjectMapper objectMapper) {
        try {
            return Optional.of(objectMapper
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(targetObject));
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
            return Optional.empty();
        }
    }

    public static Optional<String> mapScheduleRequestToJson(ScheduleRequest scheduleRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return parseObject(scheduleRequest, objectMapper);
    }

    //can be used by orchestrator to map response to json before sending it back to backend
    public static Optional<String> mapLinkResponseToJson(OrchestratorLinkResponse linkResponse) {
        return parseObject(linkResponse, new ObjectMapper());
    }

    private static <T> Optional<T> parseJson(String json, ObjectMapper objectMapper, Class<T> mappedClass) {
        try {
            return Optional.of(objectMapper.readValue(json, mappedClass));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<OrchestratorLinkResponse> mapJsonToLinkResponse(String json) {
        return parseJson(json, new ObjectMapper(), OrchestratorLinkResponse.class);
    }

    /*
    returns specific schedule request class
    can be used by orchestrator to map received request
    */
    public static Optional<ScheduleRequest> mapJsonToScheduleRequest(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return parseJson(json, objectMapper, ScheduleRequest.class);
    }

    /*
    Add another object mapping if necessary
     */
}
