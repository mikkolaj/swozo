package com.swozo.model;

import java.time.LocalDateTime;

public record ServiceLifespan(LocalDateTime startTime, LocalDateTime endTime) {
}