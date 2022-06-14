package com.swozo.databasemodel;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;

@Entity
@Table(name = "Activities")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Activity extends BaseEntity {
    private String name;
    private String description;
    private LocalDateTime dateTime;
    private String instructionsFromTeacher;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_module_id")
    private Collection<ActivityModule> modules = new LinkedList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    public void addActivityModule(ActivityModule newModuleMetadata) {
        modules.add(newModuleMetadata);
    }

}
