package com.swozo.orchestrator;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVmLifecycleManager;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GCloudProperties;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.Playbook;
import com.swozo.orchestrator.cloud.software.ssh.SshAuth;
import com.swozo.orchestrator.cloud.software.ssh.SshTarget;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Playground implements Runnable {
    private static final String zone = "europe-central2-a";
    private static final String networkName = "default";
    private static final String vmName = "super-instancja";
    private static final String machineType = "e2-medium";
    private static final String imageFamily = "debian-11";
    private static final int diskSizeGb = 10;
    private final GCloudVmLifecycleManager gCloudVmLifecycleManager;
    private final GCloudProperties properties;
    private final AnsibleRunner ansibleRunner;
    private final InternalTaskScheduler internalTaskScheduler;
    private final ScheduleRequestRepository requestRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void createInstance() {
        var vmAddress = new VmAddress(properties.project(), zone, networkName, vmName);
        var vmSpecs = new VMSpecs(machineType, imageFamily, diskSizeGb);

        try {
            gCloudVmLifecycleManager.createInstance(vmAddress, vmSpecs);
        } catch (Exception exception) {
            logger.error("Failed to create instance. {}. Exception: {}", vmAddress, exception);
        }
    }

    public void deleteInstance() {
        var vmAddress = new VmAddress(properties.project(), zone, networkName, vmName);

        try {
            gCloudVmLifecycleManager.deleteInstance(vmAddress);
        } catch (Exception exception) {
            logger.error("Failed to delete instance. {}. Exception: {}", vmAddress, exception);
        }
    }

    public void runNotebookLocally() {
        ansibleRunner.runPlaybook(
                new AnsibleConnectionDetails(
                        List.of(new SshTarget("localhost", 2222)),
                        new SshAuth("vagrant", "/home/mikolaj/IdeaProjects/swozo/orchestrator/src/main/resources/provisioning/local/.vagrant/machines/default/virtualbox/private_key")),
                Playbook.PROVISION_JUPYTER,
                10
        );
    }

    public void runNotebookRemotely() {
        ansibleRunner.runPlaybook(
                new AnsibleConnectionDetails(
                        List.of(new SshTarget("34.118.97.16", 22)),
                        new SshAuth("swozo", "/home/mikolaj/.ssh/orchestrator_id_rsa")),
                Playbook.PROVISION_JUPYTER,
                10
        );
    }

    @Override
    @Transactional
    public void run() {
        //        createInstance();
        //        deleteInstance();
        //        runNotebookLocally();
        //        runNotebookRemotely();

        var requestEntity = new ScheduleRequestEntity();

        var sd1 = new ServiceDescriptionEntity();
        var sd2 = new ServiceDescriptionEntity();
        sd1.setStatus(ServiceStatus.EXPORT_COMPLETE);
        sd2.setStatus(ServiceStatus.EXPORT_COMPLETE);
        sd1.setDynamicProperties(Collections.emptyMap());
        sd2.setDynamicProperties(Collections.emptyMap());

        requestEntity.setDiskSizeGb(1);
        requestEntity.setVmResourceId(1L);
        requestEntity.setEndTime(LocalDateTime.now());
        requestEntity.setStartTime(LocalDateTime.now());
        requestEntity.setServiceDescriptions(List.of(sd1, sd2));

        requestRepository.save(requestEntity);
        logger.info("{}", requestRepository.findScheduleRequestsWithAllServiceDescriptionsInStatus(List.of(ServiceStatus.EXPORT_COMPLETE.toString())));


        internalTaskScheduler.schedule(() -> {
            System.out.println(properties);
            System.out.println("GOOGLE_APPLICATION_CREDENTIALS=" + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
            return null;
        }, -100000000);
    }
}
