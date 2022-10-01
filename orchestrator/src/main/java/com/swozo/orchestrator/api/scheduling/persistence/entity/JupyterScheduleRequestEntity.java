package com.swozo.orchestrator.api.scheduling.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JupyterScheduleRequestEntity extends ScheduleRequestEntity {
    private String notebookLocation;
}
