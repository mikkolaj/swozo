package com.swozo.persistence;

import com.swozo.persistence.user.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "RemoteFiles")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RemoteFile extends BaseEntity {
    private String path;
    private Long sizeBytes;
    private LocalDateTime registeredAt = LocalDateTime.now();
    @ManyToOne(fetch = FetchType.EAGER)
    private User owner;

    // TODO: keep other metadata?
}
