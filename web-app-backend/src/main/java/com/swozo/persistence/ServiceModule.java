package com.swozo.persistence;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "ServiceModules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceModule extends BaseEntity {
    private String name;
    private String instructionsFromTechnicalTeacher;
    private String subject;
    private LocalDateTime creationTime = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.EAGER)
    private User creator;
    private String scheduleTypeName;
    private String scheduleTypeVersion;
    @ElementCollection
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value")
    @CollectionTable(name = "service_module_dynamic_properties", joinColumns = @JoinColumn(name = "service_module_id"))
    private Map<String, String> dynamicProperties = new HashMap<>();
    private Boolean isPublic;
    private Boolean isReady;

    /*
    insert some service specs here
     */
}
