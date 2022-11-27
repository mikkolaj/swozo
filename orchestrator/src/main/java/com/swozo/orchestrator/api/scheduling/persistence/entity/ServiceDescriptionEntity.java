package com.swozo.orchestrator.api.scheduling.persistence.entity;

import com.swozo.orchestrator.api.scheduling.control.helpers.ScheduleRequestWithServiceDescription;
import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus.SUBMITTED;

@Entity
@Table(
        name = "ServiceDescriptions",
        indexes = {
                @Index(name = "service_description_id", columnList = "id"),
                @Index(name = "service_description_schedule_request_id", columnList = "schedule_request_id")
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceDescriptionEntity extends BaseEntity {
    private Long activityModuleId;

    @Enumerated(value = EnumType.STRING)
    private ServiceTypeEntity serviceType;

    @ElementCollection
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value")
    @CollectionTable(name = "DynamicProperties", joinColumns = @JoinColumn(name = "service_description_id"))
    private Map<String, String> dynamicProperties = new HashMap<>();

    @Enumerated(value = EnumType.STRING)
    private ServiceStatus status = SUBMITTED;

    @ManyToOne
    @ToString.Exclude
    private ScheduleRequestEntity scheduleRequest;

    public ScheduleRequestWithServiceDescription toScheduleRequestWithServiceDescriptions(ScheduleRequestEntity requestEntity) {
        return new ScheduleRequestWithServiceDescription(requestEntity, this);
    }
}
