package com.swozo.databasemodel;

import com.swozo.databasemodel.users.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;

@Entity
@Table(name = "Courses")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Course extends BaseEntity {
    private String name;
    private String subject;
    private String description;
    private LocalDateTime creationTime = LocalDateTime.now();

    //FetchType.LAZY - we won't need downloading classes list everytime e.g. in courses view
    @OneToMany(fetch = FetchType.LAZY)
    private Collection<Activity> activities = new LinkedList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    private Collection<User> students = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private User teacher;

    //constructor for testing
    public Course(String name) {
        this.name = name;
    }

    public void addActivity(Activity newClass) {
        activities.add(newClass);
    }

    public void addStudent(User student) {
        students.add(student);
    }
}
