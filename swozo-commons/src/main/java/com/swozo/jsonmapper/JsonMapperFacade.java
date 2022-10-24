package com.swozo.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JsonMapperFacade {
    private static final Logger logger = LoggerFactory.getLogger(JsonMapperFacade.class);
    private final ObjectMapper objectMapper;

    @SneakyThrows(JsonProcessingException.class)
    public <T> String toJson(T data) {
        return objectMapper.writeValueAsString(data);
    }

    public <T> Optional<String> toJsonOpt(T data) {
        try {
            return Optional.of(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize " + data + " to json", e);
            return Optional.empty();
        }
    }

    @SneakyThrows({JsonProcessingException.class})
    public <T> T fromJson(String json, Class<T> type) {
        return objectMapper.readValue(json, type);
    }

    @SneakyThrows({JsonProcessingException.class})
    public <T> T fromJson(String json, TypeReference<T> type) {
        return objectMapper.readValue(json, type);
    }

    public <T> Optional<T> optFromJson(String json, Class<T> type) {
        try {
            return Optional.of(objectMapper.readValue(json, type));
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize " + json + " as " + type, e);
            return Optional.empty();
        }
    }
}
