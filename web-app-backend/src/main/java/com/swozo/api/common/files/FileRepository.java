package com.swozo.api.common.files;

import com.swozo.persistence.RemoteFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<RemoteFile, Long> {
    boolean existsByPath(String path);

    @Query("SELECT COALESCE(SUM (rf.sizeBytes), 0) FROM RemoteFile rf WHERE rf.owner.id = :userId")
    Long sumStorageBytesUsedByUser(Long userId);
}
