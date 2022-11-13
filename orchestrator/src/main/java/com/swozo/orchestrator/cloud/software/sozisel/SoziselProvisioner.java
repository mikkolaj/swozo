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
import com.swozo.orchestrator.cloud.software.LinkFormatter;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.Playbook;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.swozo.utils.LoggingUtils.logIfSuccess;

@Service
@RequiredArgsConstructor
public class SoziselProvisioner implements TimedSoftwareProvisioner {
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.SOZISEL;
    private static final int PROVISIONING_SECONDS = 900;
    private static final String MAIN_LINK_DESCRIPTION = "swozo123"; // TODO
    private static final int MINUTES = 5;
    private final TranslationsProvider translationsProvider;
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final ApplicationProperties properties;
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
        return String.format("Successfully provisioned Jupyter on resource: %s", resource);
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> createLinks(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails vmResourceDetails
    ) {

        var link = new SoziselLinksProvider(vmResourceDetails.publicIpAddress()).createLinks();
        return requestSender.getUserData(description.getActivityModuleId(), requestEntity.getId())
                .thenCompose(users -> CompletableFuture.completedFuture(
                        users.stream().map(OrchestratorUserDto::id).map(createLink(link)).toList()
                ));
    }

    private Function<Long, ActivityLinkInfo> createLink(String link) {
        return userId -> new ActivityLinkInfo(userId, link, translationsProvider.t(
                "services.jupyter.connectionInstruction",
                Map.of("password", MAIN_LINK_DESCRIPTION)
        ));
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException {

    }

    @Override
    public ServiceTypeEntity getScheduleType() {
        return SUPPORTED_SCHEDULE;
    }

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(SUPPORTED_SCHEDULE.toString(), null, Set.of(IsolationMode.SHARED));
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
                MINUTES
        );
    }
}
