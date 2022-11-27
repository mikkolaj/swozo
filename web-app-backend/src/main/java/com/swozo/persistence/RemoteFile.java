package com.swozo.persistence;

import com.swozo.persistence.user.User;
import com.swozo.persistence.user.UserFavouriteFile;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;

@Entity
@Table(name = "RemoteFiles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RemoteFile extends BaseEntity {
    @Column(columnDefinition="TEXT")
    private String path;
    private Long sizeBytes;
    private LocalDateTime registeredAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User owner;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "remoteFile")
    @ToString.Exclude
    private Collection<UserFavouriteFile> addedToFavouriteBy = new LinkedHashSet<>();

    // TODO: keep other metadata?
}
