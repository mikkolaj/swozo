package com.swozo.persistence;

import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.user.User;
import com.swozo.persistence.user.UserCourseData;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Optional;

@Entity
@Table(name = "Courses", indexes = {
        @Index(name = "idx_course_joinuuid_unq", columnList = "joinUUID", unique = true)
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_course_name", columnNames = {"name"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Course extends BaseEntity {
    private String name;
    private String subject;
    private String description;
    private String joinUUID;
    // kept in plaintext
    private String password;
    private LocalDateTime creationTime = LocalDateTime.now();
    private Boolean isPublic;
    private Boolean sandboxMode;
    private Integer expectedStudentCount;

    //FetchType.LAZY - we won't need downloading classes list everytime e.g. in courses view
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "course")
    @ToString.Exclude
    private Collection<Activity> activities = new LinkedList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "course")
    @ToString.Exclude
    private Collection<UserCourseData> students = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private User teacher;

    public void addActivity(Activity activity) {
        activities.add(activity);
        activity.setCourse(this);
    }

    public void deleteActivity(Activity activity) {
        activities.remove(activity);
    }

    public void addStudent(User student) {
        students.add(new UserCourseData(student, this));
    }

    public void deleteStudent(User student) {
        students.remove(new UserCourseData(student, this));
    }

    public Optional<String> getPassword() {
        return Optional.ofNullable(password);
    }

    public boolean isCreator(Long userId) {
        return teacher.getId().equals(userId);
    }

    public boolean isParticipant(Long userId) {
        return getStudents().stream().anyMatch(userCourseData -> userCourseData.getId().getUserId().equals(userId));
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
}
