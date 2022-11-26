package com.swozo.orchestrator.cloud.software.sozisel;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.IsolationMode;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.Playbook;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.swozo.utils.LoggingUtils.logIfSuccess;

@Service
@RequiredArgsConstructor
public class SoziselProvisioner implements TimedSoftwareProvisioner {
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.SOZISEL;
    private static final int PROVISIONING_SECONDS = 600;
    private static final int SOZISEL_SETUP_MILLISECONDS = 180000;
    private static final String MAIN_LINK_DESCRIPTION = "Use the provided link to join the Jitsi session";
    private static final int PLAYBOOK_TIMEOUT_MINUTES = 5;
    private final TranslationsProvider translationsProvider;
    private final AnsibleRunner ansibleRunner;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public CompletableFuture<List<ActivityLinkInfo>> provision(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails resource
    ) {
        return CompletableFuture.runAsync(() -> {
                    logger.info("Started provisioning Sozisel on: {}", resource);
                    runPlaybook(resource);
                }).whenComplete(logIfSuccess(logger, provisioningComplete(resource)))
                .thenCompose(x -> createLinks(requestEntity, description, resource));
    }

    private static String provisioningComplete(VmResourceDetails resource) {
        return String.format("Successfully provisioned Sozisel on resource: %s", resource);
    }

    public CompletableFuture<List<ActivityLinkInfo>> createLinks(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails vmResourceDetails
    ) {
        waitForSoziselSetup();

        SoziselLinksProvider linksProvider = new SoziselLinksProvider(vmResourceDetails.publicIpAddress());
        return requestSender.getUserData(description.getActivityModuleId(), requestEntity.getId())
                .thenApply(users -> users.stream()
                        .sorted(Comparator.comparing(OrchestratorUserDto::role).reversed()) // teacher first
                        .map(user -> createLink(user, linksProvider)).toList()
                );
    }

    private ActivityLinkInfo createLink(OrchestratorUserDto user, SoziselLinksProvider linksProvider) {
        var link = linksProvider.createLinks(user.name(), user.surname(), user.role());
        return new ActivityLinkInfo(user.id(), link, translationsProvider.t(
                "services.sozisel.connectionInstruction",
                Map.of("instruction", MAIN_LINK_DESCRIPTION)
        ));
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException {
        // service does not require any additional parameters
    }

    @Override
    public ServiceTypeEntity getServiceType() {
        return SUPPORTED_SCHEDULE;
    }

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(SUPPORTED_SCHEDULE.toString(), List.of(), Set.of(IsolationMode.SHARED), getProvisioningSeconds());
    }

    @Override
    public int getProvisioningSeconds() {
        return PROVISIONING_SECONDS;
    }

    @Override
    public Optional<String> getWorkdirToSave() {
        return Optional.empty();
    }

    private void runPlaybook(VmResourceDetails resource) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                Playbook.PROVISION_SOZISEL,
                PLAYBOOK_TIMEOUT_MINUTES
        );
    }

    @SneakyThrows
    private void waitForSoziselSetup() {
        //TODO do this more properly
        logger.info("Waiting for Sozisel to setup");
        Thread.sleep(SOZISEL_SETUP_MILLISECONDS);
    }
}
