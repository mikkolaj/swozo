package com.swozo.orchestrator.api.scheduling.persistence.entity;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public abstract class ScheduleRequestEntity extends BaseEntity {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String machineType;
    private int diskSizeGb;
    private ScheduleTypeEntity scheduleType;
}