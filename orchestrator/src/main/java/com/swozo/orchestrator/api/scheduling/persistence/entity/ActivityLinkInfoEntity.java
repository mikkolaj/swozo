package com.swozo.orchestrator.api.scheduling.persistence.entity;

import com.swozo.persistence.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "activity_links")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ActivityLinkInfoEntity extends BaseEntity {
    private String url;
    private String connectionInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleRequestId")
    private ScheduleRequestEntity scheduleRequest;
}
