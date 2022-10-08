package com.swozo.persistence;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "Files")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class File extends BaseEntity {
    private String path;
    private Long sizeBytes;
    private LocalDateTime createdAt = LocalDateTime.now();

    // TODO: keep metadata?
}
