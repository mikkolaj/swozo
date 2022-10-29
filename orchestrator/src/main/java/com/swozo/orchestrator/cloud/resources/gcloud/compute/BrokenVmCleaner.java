package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VMRepository;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Conditional(GCloudCondition.class)
@RequiredArgsConstructor
public class BrokenVmCleaner implements ApplicationListener<ApplicationReadyEvent> {
    private final VMRepository vmRepository;
    private final GCloudTimedVMProvider vmProvider;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        var deletedVms = deleteVmsWithBrokenMetadata();
        // TODO: delete VMs that have been created on cloud, but we didn't manage to register them (rather rare case)
        logger.info("Successfully deleted {} broken vms", deletedVms);
    }

    private long deleteVmsWithBrokenMetadata() {
        return vmRepository.findAllCreatedWithBrokenMetadata().stream()
                .map(vmEntity -> CheckedExceptionConverter.from(vmProvider::deleteInstance).apply(vmEntity.getId()))
                .count();
    }
}
