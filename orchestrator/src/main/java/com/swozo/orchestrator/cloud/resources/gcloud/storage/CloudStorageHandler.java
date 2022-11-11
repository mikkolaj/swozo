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
    private final BackendRequestSender requestSender;
    private final AnsibleRunner ansibleRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public CompletableFuture<Void> uploadUsersWorkdirToBucket(
            VmResourceDetails remoteHost,
            String workdirPath,
            long activityModuleId,
            long scheduleRequestId,
            long userId
    ) {
        var filename = "workdirSnapshot.zip";
        var initRequest = new InitFileUploadRequest(filename, PRETTY_BIG_SIZE);
        return requestSender.initUserFileUpload(initRequest, activityModuleId, userId).thenCompose(accessRequest -> {
            var fileToUpload = String.format("%s/%s", workdirPath, filename);

            var curlCommand = prepareUploadCurlCommand(accessRequest, fileToUpload);

            ansibleRunner.runPlaybook(
                    AnsibleConnectionDetails.from(remoteHost),
                    Playbook.UPLOAD_TO_BUCKET,
                    List.of(
                            ansibleRunner.createUserVar("command", curlCommand),
                            ansibleRunner.createUserVar("source_directory", workdirPath),
                            ansibleRunner.createUserVar("target_filename", filename)
                    ),
                    5
            );

            var uploadAccessDto = new UploadAccessDto(initRequest, accessRequest);

            return requestSender.ackUserFileUpload(uploadAccessDto, activityModuleId, scheduleRequestId, userId);
        });
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
    public CompletableFuture<Void> downloadToHost(VmResourceDetails remoteHost, String remoteFileId, String destinationPath) {
        return requestSender.getSignedDownloadUrl(remoteFileId).thenAccept(accessRequest -> {
            logger.info("Got resp for {}: {}", remoteHost, accessRequest);
            var commandParameter = new CurlCommandBuilder()
                    .addUrl(accessRequest.signedUrl()).addOutputLocation(destinationPath).build();

            ansibleRunner.runPlaybook(
                    AnsibleConnectionDetails.from(remoteHost),
                    Playbook.EXECUTE_COMMAND,
                    List.of(commandParameter),
                    5
            );
        });
    }
}
