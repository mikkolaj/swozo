package com.swozo.persistence;

import com.swozo.persistence.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    // TODO: keep other metadata?
}
