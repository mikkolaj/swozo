package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ActivityLinks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityLink extends BaseEntity {
    private String url;
    private String connectionInfo;
}
