package com.swozo.orchestrator.api.scheduling.persistence.entity;

import lombok.*;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public final class JupyterScheduleRequestEntity extends ScheduleRequestEntity {
    private String notebookLocation;
}
