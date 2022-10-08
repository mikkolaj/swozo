package com.swozo.api.common.files;

import com.swozo.persistence.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
    boolean existsByPath(String path);
}
