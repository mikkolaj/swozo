package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.Course;
import com.swozo.persistence.RemoteFile;
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
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String instructionFromTeacherHtml;
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_module_id")
    @ToString.Exclude
    private Collection<ActivityModule> modules = new LinkedList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "file.id")
    @ToString.Exclude
    private Collection<RemoteFile> publicFiles = new LinkedList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    @ToString.Exclude
    private Course course;

    public void addActivityModule(ActivityModule newModuleMetadata) {
        modules.add(newModuleMetadata);
    }

    public void removeActivityModule(ActivityModule activityModule) {
        modules.remove(activityModule);
    }

    public void addPublicFile(RemoteFile file) {
        publicFiles.add(file);
    }
}
