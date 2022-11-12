package com.swozo.orchestrator.api.backend;


import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.model.files.StorageAccessRequest;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.users.OrchestratorUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.swozo.communication.http.RequestSender.unwrap;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class BackendRequestSender {
    @Qualifier("web-server")
    private final RequestSender requestSender;
    private final BackendUriFactory factory;

    public CompletableFuture<Void> putActivityLinks(long activityModuleId, long scheduleRequestId, List<ActivityLinkInfo> links) {
        return unwrap(requestSender.sendPut(factory.createLinksUri(activityModuleId, scheduleRequestId), links, new TypeReference<>() {
        }));
    }

    public CompletableFuture<StorageAccessRequest> getSignedDownloadUrl(String encodedFileIdentifier) {
        return unwrap(requestSender.sendGet(factory.createDownloadUri(encodedFileIdentifier), new TypeReference<>() {
        }));
    }

    public CompletableFuture<StorageAccessRequest> initUserFileUpload(InitFileUploadRequest fileUploadRequest, long activityModuleId, long userId) {
        return unwrap(requestSender.sendPost(factory.createUploadInitUri(activityModuleId, userId), fileUploadRequest, new TypeReference<>() {
        }));
    }

    public CompletableFuture<Void> ackUserFileUpload(UploadAccessDto uploadAccessDto, long activityModuleId, long scheduleRequestId, long userId) {
        return unwrap(requestSender.sendPut(factory.createUploadAckUri(activityModuleId, scheduleRequestId, userId), uploadAccessDto, new TypeReference<>() {
        }));
    }

    public CompletableFuture<List<OrchestratorUserDto>> getUserData(long activityModuleId, long scheduleRequestId) {
        return unwrap(requestSender.sendGet(factory.createUserDataUrl(activityModuleId, scheduleRequestId), new TypeReference<>() {
        }));
    }

}
