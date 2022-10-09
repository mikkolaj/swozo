package com.swozo.api.common.files;

import com.swozo.api.common.files.dto.UploadAccessDto;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.common.files.request.StorageAccessRequest;
import com.swozo.api.common.files.storage.StorageProvider;
import com.swozo.api.common.files.util.FilePathGenerator;
import com.swozo.api.common.files.util.UploadValidationStrategy;
import com.swozo.persistence.RemoteFile;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.function.*;


@Service
@RequiredArgsConstructor
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StorageProvider storageProvider;
    private final FileRepository fileRepository;

    public StorageAccessRequest prepareUpload(
            InitFileUploadRequest initFileUploadRequest,
            FilePathGenerator filePathGenerator,
            UploadValidationStrategy validationStrategy
    ) {
        validationStrategy.validate();
        var filePath = filePathGenerator.generate(initFileUploadRequest.filename());

        return storageProvider.createAuthorizedUploadRequest(filePath, initFileUploadRequest.sizeBytes());
    }

    /**
     * Idempotent, new file is created and consumed by fileConsumer within a transaction scope if it doesn't already exist.
     *
     * @param uploadAccessDto - initial request and echo response to previous prepareUpload request
     * @param initialResourceSupplier - supplier providing objects that should react to upload of new file, shouldn't have any side effects
     * @param fileConsumer - consumer accepting created file and supplied objects, can provide last-line validation which can
     *                       throw an exception, causing transaction rollback and deletion of remotely stored file
     */
    @Transactional
    public <T> T acknowledgeUpload(
            UploadAccessDto uploadAccessDto,
            Supplier<T> initialResourceSupplier,
            BiFunction<RemoteFile, T, T> fileConsumer
    ) {
        var storageAccessRequest = uploadAccessDto.storageAccessRequest();
        if (!storageProvider.isValid(storageAccessRequest)) {
            // we enter here iff storageAccessRequest was spoofed (i.e. it wasn't received from prepareUpload request)
            // in this case we don't have to do anything with remote storage, because file couldn't have been uploaded,
            // and thus we shouldn't corrupt local database with info about a file that doesn't exist
            logger.warn("Verification failed for " + storageAccessRequest);
            throw new UnauthorizedException("Failed to verify upload response");
        }

        if (fileRepository.existsByPath(storageAccessRequest.filePath())) {
            return initialResourceSupplier.get();
        }

        var file = fileRepository.save(new RemoteFile(
                storageAccessRequest.filePath(),
                uploadAccessDto.initFileUploadRequest().sizeBytes(),
                LocalDateTime.now()
            )
        );

        try {
            return fileConsumer.apply(file, initialResourceSupplier.get());
        } catch (Exception exception) {
            storageProvider.cleanup(storageAccessRequest.filePath());
            throw exception;
        }
    }

    public StorageAccessRequest createDownloadRequest(RemoteFile file) {
        return storageProvider.createAuthorizedDownloadRequest(file.getPath());
    }
}
