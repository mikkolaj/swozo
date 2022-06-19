package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

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
    private String creatorName;
    private String subject;
    private LocalDateTime creationTime = LocalDateTime.now();

    /*
    insert some service specs here
     */
}
