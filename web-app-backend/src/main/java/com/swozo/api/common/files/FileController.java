package com.swozo.api.common.files;

import com.swozo.utils.StorageAccessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;

    @GetMapping("/{remoteFileId}")
    public StorageAccessRequest getDownloadSignedAccessRequestInternal(@PathVariable Long remoteFileId) {
        return fileService.createInternalDownloadRequest(remoteFileId);
    }
}
