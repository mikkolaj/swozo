package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;

@Entity
@Table(name = "Class")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Class extends BaseEntity{
    private int CourseId;
    private String dateTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "classes_modules",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    private Collection<Module> modules = new LinkedList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "class_course",
            joinColumns ={@JoinColumn(name="class_id")},
            inverseJoinColumns={@JoinColumn(name="course_id")})
    private Course course;

}
