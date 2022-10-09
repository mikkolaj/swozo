package com.swozo.persistence;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_course")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserCourseData {
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCourseId implements Serializable {
        private Long userId;
        private Long courseId;
    }

    @EmbeddedId
    private UserCourseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @ToString.Exclude
    private Course course;

    private LocalDateTime joinedAt = LocalDateTime.now();

    public UserCourseData(User user,  Course course) {
        this.user = user;
        this.course = course;
        this.id = new UserCourseId(user.getId(), course.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserCourseData that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
