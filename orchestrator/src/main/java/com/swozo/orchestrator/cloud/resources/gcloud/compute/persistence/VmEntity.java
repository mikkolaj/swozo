package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmStatus;
import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

@Entity
@Table(name = "virtual_machines")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VmEntity extends BaseEntity {
    private String project;
    private String zone;
    private String networkName;
    private String vmName;
    @Enumerated(EnumType.ORDINAL)
    private VmStatus status = VmStatus.CREATED;
}
