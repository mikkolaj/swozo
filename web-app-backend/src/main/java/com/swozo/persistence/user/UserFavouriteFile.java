package com.swozo.persistence.user;

import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.activity.Activity;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "UserFavouriteFiles")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserFavouriteFile {
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserFavouriteFileId implements Serializable {
        private Long userId;
        private Long fileId;
    }

    @EmbeddedId
    private UserFavouriteFileId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("fileId")
    @ToString.Exclude
    private RemoteFile remoteFile;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    public UserFavouriteFile(User user, RemoteFile remoteFile, Activity activity) {
        this.user = user;
        this.remoteFile = remoteFile;
        this.activity = activity;
        this.id = new UserFavouriteFileId(user.getId(), remoteFile.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserFavouriteFile that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
