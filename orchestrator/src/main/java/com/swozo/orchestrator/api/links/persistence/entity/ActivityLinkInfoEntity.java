package com.swozo.orchestrator.api.links.persistence.entity;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "activity_links", indexes = {@Index(name = "idx_schedule_request_id", columnList = "scheduleRequestId")})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityLinkInfoEntity extends BaseEntity {
    private String url;
    private String connectionInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleRequestId")
    @ToString.Exclude
    private ScheduleRequestEntity scheduleRequest;
}
