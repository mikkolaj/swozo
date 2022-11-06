package com.swozo.orchestrator.cloud.resources.gcloud.storage;

import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.model.files.StorageAccessRequest;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.curl.CurlCommandBuilder;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.Playbook;
import com.swozo.orchestrator.cloud.storage.BucketHandler;
import com.swozo.orchestrator.cloud.storage.BucketOperationFailed;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public void uploadUsersWorkdirToBucket(
            VMResourceDetails remoteHost,
            String workdirPath,
            long activityModuleId,
            long scheduleRequestId,
            long userId
    ) {
        var filename = "workdirSnapshot.zip";
        var initRequest = new InitFileUploadRequest(filename, PRETTY_BIG_SIZE);
        var accessRequest = CheckedExceptionConverter.from(
                () -> requestSender.initUserFileUpload(initRequest, activityModuleId, userId).get(),
                BucketOperationFailed::new
        ).get();

        var curlCommand = prepareUploadCurlCommand(accessRequest);

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

        CheckedExceptionConverter.from(
                () -> requestSender.ackUserFileUpload(uploadAccessDto, activityModuleId, scheduleRequestId, userId)
                        .get(),
                BucketOperationFailed::new
        ).get();
    }

    private String prepareUploadCurlCommand(StorageAccessRequest accessRequest) {
        var curlCommandBuilder =
                new CurlCommandBuilder().addUrl(accessRequest.signedUrl()).addHttpMethod(accessRequest.httpMethod());

        for (var header : accessRequest.httpHeaders().entrySet()) {
            curlCommandBuilder.addHttpHeader(header.getKey(), header.getValue());
        }

        return curlCommandBuilder.build();
    }

    @Override
    public void downloadToHost(VMResourceDetails remoteHost, String remoteFileId, String destinationPath) {
        var accessRequest = CheckedExceptionConverter.from(
                () -> requestSender.getSignedDownloadUrl(remoteFileId).get(),
                BucketOperationFailed::new
        ).get();
        logger.info("Got resp for {}: {}", remoteHost, accessRequest);
        var commandParameter =
                String.format("command='curl %s --output %s'", accessRequest.signedUrl(), destinationPath);

        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(remoteHost),
                Playbook.EXECUTE_COMMAND,
                List.of(commandParameter),
                5
        );
    }
}
