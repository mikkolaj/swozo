package com.swozo.api.common.files;

import com.swozo.model.utils.StorageAccessRequest;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.swozo.config.Config.*;
import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping(FILES)
@RequiredArgsConstructor
@SecurityRequirement(name = ACCESS_TOKEN)
public class FileController {
    private final FileService fileService;

    @GetMapping(INTERNAL + DOWNLOAD + "/{remoteFileId}")
    public StorageAccessRequest getDownloadSignedAccessRequestInternal(@PathVariable Long remoteFileId) {
        return fileService.createInternalDownloadRequest(remoteFileId);
    }

    @GetMapping(EXTERNAL + DOWNLOAD + "/{remoteFileId}")
    public StorageAccessRequest getDownloadSignedAccessRequestExternal(AccessToken accessToken, @PathVariable Long remoteFileId) {
        return fileService.createExternalDownloadRequest(remoteFileId, accessToken.getUserId());
    }
}
