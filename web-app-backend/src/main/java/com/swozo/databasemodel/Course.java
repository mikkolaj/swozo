package com.swozo.databasemodel;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

@Entity
@Table(name = "Course")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Course extends BaseEntity{
    private String name;

//    @OneToMany(mappedBy = "course")
//    @Fetch(FetchMode.JOIN)
//    @JsonIgnore
//    private Collection<Class> classes = new LinkedList<>();
}
