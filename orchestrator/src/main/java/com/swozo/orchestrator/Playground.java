package com.swozo.orchestrator;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVMLifecycleManager;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GCloudProperties;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.ssh.SshTarget;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    private final GCloudVMLifecycleManager gCloudVmLifecycleManager;
    private final GCloudProperties properties;
    private final AnsibleRunner ansibleRunner;
    private final InternalTaskScheduler internalTaskScheduler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void createInstance() {
        var vmAddress = new VMAddress(properties.project(), zone, networkName, vmName);
        var vmSpecs = new VMSpecs(machineType, imageFamily, diskSizeGb);

        try {
            gCloudVmLifecycleManager.createInstance(vmAddress, vmSpecs);
        } catch (Exception exception) {
            logger.error("Failed to create instance. {}. Exception: {}", vmAddress, exception);
        }
    }

    public void deleteInstance() {
        var vmAddress = new VMAddress(properties.project(), zone, networkName, vmName);

        try {
            gCloudVmLifecycleManager.deleteInstance(vmAddress);
        } catch (Exception exception) {
            logger.error("Failed to delete instance. {}. Exception: {}", vmAddress, exception);
        }
    }

    public void runNotebookLocally() throws InterruptedException {
        ansibleRunner.runPlaybook(
                new AnsibleConnectionDetails(
                        List.of(new SshTarget("localhost", 2222)),
                        "vagrant",
                        "/home/mikolaj/IdeaProjects/swozo/orchestrator/src/main/resources/provisioning/local/.vagrant/machines/default/virtualbox/private_key"),
                "/home/mikolaj/IdeaProjects/swozo/orchestrator/src/main/resources/provisioning/software/jupyter/prepare-and-run-jupyter.yml",
                10
        );
    }

    public void runNotebookRemotely() throws InterruptedException {
        ansibleRunner.runPlaybook(
                new AnsibleConnectionDetails(
                        List.of(new SshTarget("34.118.97.16", 22)),
                        "swozo",
                        "/home/mikolaj/.ssh/orchestrator_id_rsa"),
                "/home/mikolaj/IdeaProjects/swozo/orchestrator/src/main/resources/provisioning/software/jupyter/prepare-and-run-jupyter.yml",
                10
        );
    }

    @Override
    public void run() {
        //        createInstance();
        //        deleteInstance();
        //        runNotebookLocally();
        //        runNotebookRemotely();
        internalTaskScheduler.schedule(() -> {
            System.out.println(properties);
            System.out.println("GOOGLE_APPLICATION_CREDENTIALS=" + System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
            return null;
        }, -100000000);
    }
}
