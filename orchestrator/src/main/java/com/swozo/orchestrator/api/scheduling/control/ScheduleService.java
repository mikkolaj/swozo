package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    // TODO: probably will change if we decide to execute tasks (e.g. saving user's files) before deleting the instance
    private static final int CLEANUP_SECONDS = 0;
    private final InternalTaskScheduler scheduler;
    private final TimedVMProvider timedVmProvider;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final ScheduleRequestTracker scheduleRequestTracker;
    private final ScheduleRequestMapper requestMapper;
    private final TimingService timingService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // TODO: add retrying and cleaning of received, but not fulfilled requests
    public ScheduleResponse schedule(ScheduleRequest request) {
        var provisioner = provisionerFactory.getProvisioner(request.scheduleType());
        provisioner.validateParameters(request.dynamicProperties());
        var requestEntity = scheduleRequestTracker.startTracking(request);
        delegateScheduling(requestEntity, provisioner);
        return new ScheduleResponse(requestEntity.getId());
    }

    private void delegateScheduling(ScheduleRequestEntity requestEntity, TimedSoftwareProvisioner provisioner) {
        var offset = timingService.getSchedulingOffset(requestMapper.toDto(requestEntity), provisioner.getProvisioningSeconds());
        logger.info("Scheduling request [id: {}] in {} seconds.", requestEntity.getId(), offset);
        scheduler.schedule(() -> scheduleCreationAndDeletion(requestEntity, provisioner), offset);
    }

    public List<ServiceConfig> getSupportedServices() {
        return provisionerFactory.getAllProvisioners().stream().map(TimedSoftwareProvisioner::getServiceConfig).toList();
    }

    private Void scheduleCreationAndDeletion(
            ScheduleRequestEntity request,
            TimedSoftwareProvisioner provisioner
    ) throws InterruptedException {
        try {
            scheduleRequestTracker.updateStatus(request.getId(), VM_CREATING);
            timedVmProvider
                    .createInstance(requestMapper.toDto(request).psm())
                    .thenAccept(provisionSoftwareAndScheduleDeletion(request, provisioner))
                    .get();
        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case VMOperationFailed ex -> handleFailedVmCreation(request, ex);
                default -> handleUnexpectedException(request, e.getCause(), true);
            }
        } catch (Exception e) {
            handleUnexpectedException(request, e, false);
            throw e;
        }
        return null;
    }

    private void handleFailedVmCreation(ScheduleRequestEntity request, VMOperationFailed ex) {
        logger.error("Error while creating instance. Request: {}", request, ex);
        scheduleRequestTracker.markAsFailure(request.getId());
    }

    private void handleUnexpectedException(ScheduleRequestEntity request, Throwable ex, boolean log) {
        if (log) logger.error("Unexpected exception. Request: {}", request, ex);
        scheduleRequestTracker.markAsFailure(request.getId());
    }

    private Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequestEntity request,
            TimedSoftwareProvisioner provisioner
    ) {
        return resourceDetails -> {
            try {
                scheduleRequestTracker.updateStatus(request.getId(), PROVISIONING);
                scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
                var links = delegateProvisioning(request, provisioner, resourceDetails);
                scheduleRequestTracker.updateStatus(request.getId(), READY);
                scheduleRequestTracker.saveLinks(request.getId(), links);
                scheduler.schedule(
                        () -> deleteInstance(request, resourceDetails),
                        timingService.getDeletionOffset(requestMapper.toDto(request), CLEANUP_SECONDS));
            } catch (ProvisioningFailed e) {
                logger.error("Provisioning software on: {} failed.", resourceDetails, e);
                scheduleRequestTracker.markAsFailure(request.getId());
            }
        };
    }

    private static List<ActivityLinkInfo> delegateProvisioning(ScheduleRequestEntity request, TimedSoftwareProvisioner provisioner, VMResourceDetails resourceDetails) {
        return CheckedExceptionConverter.from(
                () -> provisioner.provision(resourceDetails, request.getDynamicProperties()),
                ProvisioningFailed::new
        ).get();
    }

    private Void deleteInstance(ScheduleRequestEntity request, VMResourceDetails connectionDetails) throws InterruptedException {
        try {
            timedVmProvider.deleteInstance(connectionDetails.internalResourceId())
                    .thenRun(() -> scheduleRequestTracker.updateStatus(request.getId(), DELETED));
        } catch (VMOperationFailed e) {
            logger.error("Deleting instance failed!", e);
        }
        return null;
    }
}
