package com.swozo.persistence;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "ActivityInstructions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityInstruction extends BaseEntity {
    private String sanitizedHtmlData;
}
