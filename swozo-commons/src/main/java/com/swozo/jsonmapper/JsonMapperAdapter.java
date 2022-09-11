package com.swozo.jsonmapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JsonMapperAdapter {
    private static final Logger logger = LoggerFactory.getLogger(JsonMapperAdapter.class);
    private final ObjectMapper proxy;

    @SneakyThrows(JsonProcessingException.class)
    public <T> String toJson(T data) {
        return proxy.writeValueAsString(data);
    }

    public <T> Optional<String> toJsonOpt(T data) {
        try {
            return Optional.of(proxy.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize " + data + " to json", e);
            return Optional.empty();
        }
    }

    @SneakyThrows({JsonProcessingException.class})
    public <T> T fromJson(String json, Class<T> type) {
        return proxy.readValue(json, type);
    }

    public <T> Optional<T> optFromJson(String json, Class<T> type) {
        try {
            return Optional.of(proxy.readValue(json, type));
        } catch (JsonProcessingException e) {
            logger.error("Failed to deserialize " + json + " as " + type, e);
            return Optional.empty();
        }
    }
}
