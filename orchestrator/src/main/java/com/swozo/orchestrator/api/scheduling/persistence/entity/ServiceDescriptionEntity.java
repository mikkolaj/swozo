package com.swozo.orchestrator.api.scheduling.persistence.entity;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus.SUBMITTED;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceDescriptionEntity extends BaseEntity {
    private ServiceTypeEntity serviceType;

    @ElementCollection
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value")
    @CollectionTable(name = "dynamic_properties", joinColumns = @JoinColumn(name = "service_description_id"))
    private Map<String, String> dynamicProperties = new HashMap<>();

    private RequestStatus status = SUBMITTED;
}
