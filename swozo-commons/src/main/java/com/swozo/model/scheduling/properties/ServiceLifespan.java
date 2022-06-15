package com.swozo.model.scheduling.properties;

import java.time.LocalDateTime;

public record ServiceLifespan(LocalDateTime startTime, LocalDateTime endTime) {
}
