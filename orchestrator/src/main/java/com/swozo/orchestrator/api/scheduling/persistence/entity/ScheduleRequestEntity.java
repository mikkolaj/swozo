package com.swozo.orchestrator.api.scheduling.persistence.entity;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "ScheduleRequests")
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

    @OneToMany(cascade = {CascadeType.ALL})
    @JoinColumn(name = "schedule_request_id")
    private List<ServiceDescriptionEntity> serviceDescriptions;
    private Long vmResourceId;

    public Optional<Long> getVmResourceId() {
        return Optional.ofNullable(vmResourceId);
    }
}
