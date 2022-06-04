package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

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

    /*
    insert some service specs here
     */
}
