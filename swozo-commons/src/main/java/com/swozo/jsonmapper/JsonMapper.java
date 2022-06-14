package com.swozo.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.swozo.model.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;

import java.util.Optional;

public class JsonMapper {
    public static Optional<String> mapScheduleRequestToJson(ScheduleRequest scheduleRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            return Optional.of(objectMapper
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(scheduleRequest));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
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
        try {
            ScheduleRequest mappedRequest = objectMapper.readValue(json, ScheduleRequest.class);
            return Optional.of(mappedRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    //can be used by orchestrator to map response to json before sending it back to backend
    public static Optional<String> mapLinkResponseToJson(OrchestratorLinkResponse linkResponse) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return Optional.of(mapper
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(linkResponse));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public static Optional<OrchestratorLinkResponse> mapJsonToLinkResponse(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OrchestratorLinkResponse mappedResponse = objectMapper.readValue(json, OrchestratorLinkResponse.class);
            return Optional.of(mappedResponse);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /*
    Add another object mapping if necessary
     */
}
