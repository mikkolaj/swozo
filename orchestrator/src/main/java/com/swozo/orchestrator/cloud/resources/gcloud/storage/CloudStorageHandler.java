package com.swozo.orchestrator.cloud.resources.gcloud.storage;

import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.model.files.StorageAccessRequest;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.software.curl.CurlCommandBuilder;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.Playbook;
import com.swozo.orchestrator.cloud.software.ssh.SshAuth;
import com.swozo.orchestrator.cloud.software.ssh.SshService;
import com.swozo.orchestrator.cloud.software.ssh.SshTarget;
import com.swozo.orchestrator.cloud.storage.BucketHandler;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Conditional(GCloudCondition.class)
@RequiredArgsConstructor
public class CloudStorageHandler implements BucketHandler {
    // TODO: read the filesize
    private static final int PRETTY_BIG_SIZE = 100000000;
    private static final String WORKDIR_SNAPSHOT_FILENAME = "workdirSnapshot.zip";
    private static final String SNAPSHOT_PATH_TEMPLATE = "/tmp/%s";
    private final BackendRequestSender requestSender;
    private final AnsibleRunner ansibleRunner;
    private final SshService sshService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CompletableFuture<Void> uploadUsersWorkdirToBucket(
            VmResourceDetails remoteHost,
            String workdirPath,
            long activityModuleId,
            long scheduleRequestId,
            long userId
    ) {
        var archiveTargetFileName = createWorkdirSnapshotFilename(scheduleRequestId, activityModuleId, userId);
        var fileToUpload = String.format(SNAPSHOT_PATH_TEMPLATE, archiveTargetFileName);
        createWorkdirArchive(remoteHost, workdirPath, archiveTargetFileName);
        var initRequest = createInitRequest(remoteHost, fileToUpload);
        return requestSender.initUserFileUpload(initRequest, activityModuleId, userId).thenCompose(accessRequest -> {
            uploadFileToBucket(remoteHost, fileToUpload, accessRequest);

            var uploadAccessDto = new UploadAccessDto(initRequest, accessRequest);
            return requestSender.ackUserFileUpload(uploadAccessDto, activityModuleId, scheduleRequestId, userId);
        });
    }

    private void createWorkdirArchive(VmResourceDetails remoteHost, String workdirPath, String targetPath) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(remoteHost),
                Playbook.CREATE_ARCHIVE,
                List.of(
                        ansibleRunner.createUserVar("source_directory", workdirPath),
                        ansibleRunner.createUserVar("target_filename", targetPath)
                ),
                getUploadTimeoutMinutes(5)
        );
    }

    private InitFileUploadRequest createInitRequest(VmResourceDetails remoteHost, String fileToUpload) {
        var fileSize = sshService.getFileSize(SshTarget.from(remoteHost), SshAuth.from(remoteHost), fileToUpload);
        logger.info("Size of the file to export from {} is: {}", remoteHost, fileSize);
        return new InitFileUploadRequest(WORKDIR_SNAPSHOT_FILENAME, fileSize);
    }

    private void uploadFileToBucket(VmResourceDetails remoteHost, String fileToUpload, StorageAccessRequest accessRequest) {
        var curlCommand = prepareUploadCurlCommand(accessRequest, fileToUpload);

        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(remoteHost),
                Playbook.EXECUTE_COMMAND,
                List.of(ansibleRunner.createUserVar("command", curlCommand)),
                getUploadTimeoutMinutes(5)
        );
    }

    private String prepareUploadCurlCommand(StorageAccessRequest accessRequest, String fileToUpload) {
        var curlCommandBuilder = new CurlCommandBuilder().addUrl(accessRequest.signedUrl())
                .addHttpMethod(accessRequest.httpMethod())
                .addFileSource(fileToUpload);

        for (var header : accessRequest.httpHeaders().entrySet()) {
            curlCommandBuilder.addHttpHeader(header.getKey(), header.getValue());
        }

        return curlCommandBuilder.build();
    }

    @Override
    public CompletableFuture<Void> downloadToHost(VmResourceDetails remoteHost, String remoteFileId, String destinationPath, String fileOwner) {
        return requestSender.getSignedDownloadUrl(remoteFileId).thenAccept(accessRequest -> {
            logger.info("Got resp for {}: {}", remoteHost, accessRequest);
            var commandParameter = new CurlCommandBuilder()
                    .addUrl(accessRequest.signedUrl()).addOutputLocation(destinationPath).build();

            ansibleRunner.runPlaybook(
                    AnsibleConnectionDetails.from(remoteHost),
                    Playbook.EXECUTE_COMMAND,
                    List.of(
                            ansibleRunner.createUserVar("command", commandParameter),
                            ansibleRunner.createUserVar("user", fileOwner)
                    ),
                    getDownloadTimeoutMinutes(PRETTY_BIG_SIZE)
            );
        });
    }

    public int getUploadTimeoutMinutes(int fileSizeBytes) {
        // TODO after we implement reading size
        return 10;
    }

    public int getDownloadTimeoutMinutes(int fileSizeBytes) {
        // TODO after orchestrator receives size of file to download
        return 10;
    }

    private String createWorkdirSnapshotFilename(long scheduleRequestId, long activityModuleId, long userId) {
       return String.format("%s_%s_%s_%s", activityModuleId, scheduleRequestId, userId, WORKDIR_SNAPSHOT_FILENAME);
    }
}
