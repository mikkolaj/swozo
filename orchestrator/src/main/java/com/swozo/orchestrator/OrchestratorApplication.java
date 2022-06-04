package com.swozo.orchestrator;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVMLifecycleManager;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class OrchestratorApplication {
    @Autowired
    GCloudVMLifecycleManager GCloudVmLifecycleManager;

    Logger logger = LoggerFactory.getLogger(OrchestratorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(OrchestratorApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        System.out.println("Hello world, I have just started up");
        createAndDeleteInstance();
    }

    public void createAndDeleteInstance() {
        var project = "hybrid-text-350213";
        var zone = "us-west2-a";
        var networkName = "default";
        var vmName = "super-instancja";

        var vmAddress = new VMAddress(project, zone, networkName, vmName);
        var vmSpecs = new VMSpecs("e2-micro", "debian-11", 10);

        try {
            var operation = GCloudVmLifecycleManager.createInstance(vmAddress, vmSpecs);
            var response = operation.get(3, TimeUnit.MINUTES);

            if (response.hasError()) {
                logger.error("Instance creation failed! " + response);
            } else {
                logger.info("Operation Status: " + response.getStatus());
            }
        } catch (Exception exception) {
            logger.error("Failed to create instance. " + vmAddress + " " + exception);
        }

        try {
            var operation = GCloudVmLifecycleManager.deleteInstance(vmAddress);
            var response = operation.get(3, TimeUnit.MINUTES);

            if (response.hasError()) {
                logger.error("Instance creation failed! " + response);
            } else {
                logger.info("Operation Status: " + response.getStatus());
            }
        } catch (Exception exception) {
            logger.error("Failed to delete instance. " + vmAddress + " " + exception);
        }
    }

}
