package com.swozo.orchestrator.api.scheduling.persistence.entity;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ScheduleRequestEntity extends BaseEntity {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String machineType;
    private int diskSizeGb;
    private ScheduleTypeEntity scheduleType;

    @ElementCollection
    @MapKeyColumn(name = "name")
    @Column(name = "value")
    @CollectionTable(name = "dynamic_properties", joinColumns = @JoinColumn(name = "example_id"))
    private Map<String, String> dynamicProperties = new HashMap<>();

    private RequestStatus status = RequestStatus.SUBMITTED;
    private Long vmResourceId;

    public Optional<Long> getVmResourceId() {
        return Optional.ofNullable(vmResourceId);
    }
}
