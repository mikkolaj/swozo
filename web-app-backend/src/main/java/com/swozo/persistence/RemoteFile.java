package com.swozo.persistence;

import lombok.*;

import javax.persistence.Entity;
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
    private LocalDateTime createdAt = LocalDateTime.now();

    // TODO: keep metadata?
}
