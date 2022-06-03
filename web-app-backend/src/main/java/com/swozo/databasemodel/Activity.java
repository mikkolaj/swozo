package com.swozo.databasemodel;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
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
    private String dateTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private Collection<Module> modules = new LinkedList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    public void addModule(Module newModule) {
        modules.add(newModule);
    }

}
