package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMStatus;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VMRepository;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class BrokenVmCleaner {
    private final VMRepository vmRepository;
    private final ScheduleRequestRepository requestRepository;
    private final GCloudTimedVMProvider vmProvider;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostConstruct
    public void deleteVmsInBadState() {
        var deletedVms = deleteVmsWithBrokenMetadata();
        // TODO: delete VMs that have been created on cloud, but we didn't manage to register them (rather rare case)
        logger.info("Successfully deleted {} broken vms", deletedVms);
    }

    @Transactional
    protected long deleteVmsWithBrokenMetadata() {
        return vmRepository.findAllByStatusEquals(VMStatus.CREATED)
                .filter(vmEntity -> !requestRepository.existsByVmResourceIdEquals(vmEntity.getId()))
                .map(vmEntity -> CheckedExceptionConverter.from(vmProvider::deleteInstance).apply(vmEntity.getId()))
                .count();
    }
}
