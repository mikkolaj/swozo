package com.swozo.api.common.files;

import com.swozo.api.common.files.dto.UploadAccessDto;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.common.files.storage.StorageProvider;
import com.swozo.api.common.files.util.FilePathGenerator;
import com.swozo.api.common.files.util.UploadValidationStrategy;
import com.swozo.api.web.exceptions.types.files.FileNotFoundException;
import com.swozo.config.properties.StorageProperties;
import com.swozo.mapper.FileMapper;
import com.swozo.model.utils.StorageAccessRequest;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.user.User;
import com.swozo.security.exceptions.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.function.BiFunction;
import java.util.function.Supplier;


@Service
@RequiredArgsConstructor
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StorageProvider storageProvider;
    private final FileRepository fileRepository;
    private final StorageProperties storageProperties;
    private final FileMapper fileMapper;
    private final FilePathProvider filePathProvider;

    public StorageAccessRequest prepareExternalUpload(
            InitFileUploadRequest initFileUploadRequest,
            FilePathGenerator filePathGenerator,
            UploadValidationStrategy validationStrategy
    ) {
        validationStrategy.validate();
        filePathProvider.validateFilename(initFileUploadRequest.filename());
        var filePath = filePathGenerator.generate(initFileUploadRequest.filename());

        return storageProvider.createAuthorizedUploadRequest(
                storageProperties.webBucket().name(),
                filePath,
                initFileUploadRequest.sizeBytes(),
                storageProperties.externalUploadValidity()
        );
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
    public <T> T acknowledgeExternalUpload(
            User owner,
            UploadAccessDto uploadAccessDto,
            Supplier<T> initialResourceSupplier,
            BiFunction<RemoteFile, T, T> fileConsumer
    ) {
        var storageAccessRequest = uploadAccessDto.storageAccessRequest();
        validateStorageAccessRequest(storageAccessRequest);

        if (fileRepository.existsByPath(storageAccessRequest.filePath())) {
            return initialResourceSupplier.get();
        }

        var file = fileRepository.save(fileMapper.toPersistence(uploadAccessDto, owner));

        try {
            return fileConsumer.apply(file, initialResourceSupplier.get());
        } catch (Exception exception) {
            storageProvider.cleanup(storageProperties.webBucket().name(), storageAccessRequest.filePath());
            throw exception;
        }
    }

    public RemoteFile acknowledgeExternalUploadWithoutTxn(User owner, UploadAccessDto uploadAccessDto) {
        validateStorageAccessRequest(uploadAccessDto.storageAccessRequest());
        return fileRepository.save(fileMapper.toPersistence(uploadAccessDto, owner));
    }

    public StorageAccessRequest createExternalDownloadRequest(RemoteFile file) {
        return storageProvider.createAuthorizedDownloadRequest(
                storageProperties.webBucket().name(),
                file.getPath(),
                storageProperties.externalDownloadValidity()
        );
    }

    public StorageAccessRequest createExternalDownloadRequest(Long remoteFileId, Long downloaderId) {
        var file = fileRepository.findById(remoteFileId).orElseThrow(() -> FileNotFoundException.globally(remoteFileId));
        if (!file.getOwner().getId().equals(downloaderId)) {
            throw new UnauthorizedException("You are unauthorized to download this file.");
        }
        return createExternalDownloadRequest(file);
    }

    public StorageAccessRequest createInternalDownloadRequest(Long remoteFileId) {
        var file = fileRepository.findById(remoteFileId).orElseThrow();
        return storageProvider.createAuthorizedDownloadRequest(
                storageProperties.webBucket().name(),
                file.getPath(),
                storageProperties.internalDownloadValidity()
        );
    }

    private void validateStorageAccessRequest(StorageAccessRequest storageAccessRequest) {
        if (!storageProvider.isValid(storageAccessRequest)) {
            // we enter here iff storageAccessRequest was spoofed (i.e. it wasn't received from prepareUpload request)
            // in this case we don't have to do anything with remote storage, because file couldn't have been uploaded,
            // and thus we shouldn't corrupt local database with info about a file that doesn't exist
            logger.warn("Verification failed for " + storageAccessRequest);
            throw new UnauthorizedException("Failed to verify upload response");
        }
    }

    public String encodeUniqueIdentifier(RemoteFile file) {
        return file.getId().toString();
    }

    public RemoteFile decodeUniqueIdentifier(String encodedIdentifier) {
        return fileRepository.findById(Long.valueOf(encodedIdentifier)).orElseThrow();
    }

    /**
     * Called when removal of a file is caused not by a direct user action. File is not guaranteed to be deleted
     * from both local and remote storage but logs are guaranteed to be maintained for later retry.
     */
    public void removeFileInternally(RemoteFile file) {
        // TODO assert proper logging
        fileRepository.delete(file);
        storageProvider.cleanup(storageProperties.webBucket().name(), file.getPath());
    }
}
