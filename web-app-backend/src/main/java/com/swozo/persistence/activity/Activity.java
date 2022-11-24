package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.Course;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.user.User;
import lombok.*;

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
    @Column(columnDefinition="TEXT")
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    @Column(columnDefinition="TEXT")
    private String instructionFromTeacherHtml;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "activity")
    @ToString.Exclude
    private Collection<ActivityModule> modules = new LinkedList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "ActivityPublicFiles",
            joinColumns = @JoinColumn(name = "activity_id"),
            inverseJoinColumns = @JoinColumn(name = "file_id")
    )
    @ToString.Exclude
    private Collection<RemoteFile> publicFiles = new LinkedList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    private Course course;

    public void addActivityModule(ActivityModule activityModule) {
        activityModule.setActivity(this);
        modules.add(activityModule);
    }

    public void removeActivityModule(ActivityModule activityModule) {
        modules.remove(activityModule);
    }

    public void addPublicFile(RemoteFile file) {
        publicFiles.add(file);
    }

    public User getTeacher() {
        return course.getTeacher();
    }
}
