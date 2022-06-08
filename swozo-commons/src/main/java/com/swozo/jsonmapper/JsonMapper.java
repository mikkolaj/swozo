package com.swozo.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.swozo.model.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;

import java.util.Optional;

public class JsonMapper {
    public static Optional<String> mapScheduleRequestToJson(ScheduleRequest scheduleRequest) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            return Optional.of(mapper
                    .writer()
                    .withDefaultPrettyPrinter()
                    .writeValueAsString(scheduleRequest));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    //TODO Json to schedule request - adding enum type required to create inherited classes?

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
