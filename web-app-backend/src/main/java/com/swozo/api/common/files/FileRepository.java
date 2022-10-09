package com.swozo.api.common.files;

import com.swozo.persistence.RemoteFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<RemoteFile, Long> {
    boolean existsByPath(String path);
}
