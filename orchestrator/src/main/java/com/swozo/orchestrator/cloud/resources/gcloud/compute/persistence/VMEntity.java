package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMStatus;
import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "virtual_machines")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VMEntity extends BaseEntity {
    private String project;
    private String zone;
    private String networkName;
    private String vmName;
    private VMStatus status = VMStatus.CREATING;
}
