package com.swozo.orchestrator.cloud.software.sozisel;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.api.BackendRequestSender;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.LinkFormatter;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.NotebookFailed;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
class SoziselProvisioner implements TimedSoftwareProvisioner {
    private static final ScheduleType SUPPORTED_SCHEDULE = ScheduleType.SOZISEL;
    private static final int PROVISIONING_SECONDS = 600;
    private static final int MINUTES = 5;
    private static final String SOZISEL_PORT = "4000";
    private final TranslationsProvider translationsProvider;
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final ApplicationProperties properties;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public List<ActivityLinkInfo> provision(VMResourceDetails resourceDetails, Map<String, String> dynamicParameters) throws InterruptedException, ProvisioningFailed {
        try {
            logger.info("Started provisioning Sozisel on: {}", resourceDetails);
            runPlaybook(resourceDetails);
//            handleParameters(dynamicParameters, resourceDetails);
            logger.info("Successfully provisioned Sozisel on resource: {}", resourceDetails);
            return createLinks(resourceDetails);
        } catch (InvalidParametersException | NotebookFailed e) {
            throw new ProvisioningFailed(e);
        }
    }

    @Override
    public List<ActivityLinkInfo> createLinks(VMResourceDetails resourceDetails) {
        return null;
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException {

    }

    @Override
    public ScheduleType getScheduleType() {
        return SUPPORTED_SCHEDULE;
    }

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(SUPPORTED_SCHEDULE.toString(), null);
    }

    @Override
    public int getProvisioningSeconds() {
        return PROVISIONING_SECONDS;
    }

    private void runPlaybook(VMResourceDetails resource) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                properties.soziselPlaybookPath(),
                MINUTES
        );
    }
}
