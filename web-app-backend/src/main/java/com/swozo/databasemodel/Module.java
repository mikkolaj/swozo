package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Modules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Module extends BaseEntity {
    private String name;
}