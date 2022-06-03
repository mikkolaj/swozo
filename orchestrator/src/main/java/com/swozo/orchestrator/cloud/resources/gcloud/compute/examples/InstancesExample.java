package com.swozo.orchestrator.cloud.resources.gcloud.compute.examples;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.compute.v1.*;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class InstancesExample {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException, TimeoutException {
//        SpringApplication.run(OrchestratorApplication.class, args);

        var project = "hybrid-text-350213";
        var zone = "us-west2-a";
//        createInstance(project, zone, "super-instancja");
        listInstances(project, zone);
        deleteInstance(project, zone, "super-instancja");
    }

    // List all instances in the given zone in the specified project ID.
    public static void listInstances(String project, String zone) throws IOException {
        try (InstancesClient instancesClient = InstancesClient.create()) {
            System.out.printf("Listing instances from %s in %s:%n", project, zone);
            for (Instance zoneInstance : instancesClient.list(project, zone).iterateAll()) {
                System.out.println(zoneInstance.getName());
            }
            System.out.println("####### Listing instances complete #######");
        }
    }

    public static void editInstance(String project, String zone, String instanceName) throws IOException {
        try (InstancesClient instancesClient = InstancesClient.create()) {

        }
    }

    public static void createInstance(String project, String zone, String instanceName)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Below are sample values that can be replaced.
        // machineType: machine type of the VM being created.
        // *   This value uses the format zones/{zone}/machineTypes/{type_name}.
        // *   For a list of machine types, see https://cloud.google.com/compute/docs/machine-types
        // imageFamily: path to the operating system image to mount.
        // *   For details about images you can mount, see https://cloud.google.com/compute/docs/images
        // diskSizeGB: storage size of the boot disk to attach to the instance.
        // networkName: network interface to associate with the instance.
        String machineType = String.format("zones/%s/machineTypes/e2-micro", zone);
        String sourceImage = String.format("projects/debian-cloud/global/images/family/%s", "debian-11");
        long diskSizeGb = 10L;
        String networkName = "default";

        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the `instancesClient.close()` method on the client to safely
        // clean up any remaining background resources.
        try (InstancesClient instancesClient = InstancesClient.create()) {
            // Instance creation requires at least one persistent disk and one network interface.
            var diskParameters = AttachedDiskInitializeParams.newBuilder()
                    .setSourceImage(sourceImage)
                    .setDiskSizeGb(diskSizeGb)
                    .build();

            AttachedDisk disk = AttachedDisk.newBuilder()
                    .setBoot(true)
                    .setAutoDelete(true)
                    .setType(AttachedDisk.Type.PERSISTENT.toString())
                    .setDeviceName("disk-1")
                    .setInitializeParams(diskParameters)
                    .build();

            // Use the network interface provided in the networkName argument.
            var networkAccessConfig = AccessConfig.newBuilder()
                    .setType("ONE_TO_ONE_NAT")
                    .setName("External NAT")
                    .build();

            NetworkInterface networkInterface = NetworkInterface.newBuilder()
                    .setName(networkName)
                    .addAccessConfigs(networkAccessConfig)
                    .build();

            // Bind `instanceName`, `machineType`, `disk`, and `networkInterface` to an instance.
            Instance instanceResource = Instance.newBuilder()
                    .setName(instanceName)
                    .setMachineType(machineType)
                    .addDisks(disk)
                    .addNetworkInterfaces(networkInterface)
                    .build();

            System.out.printf("Creating instance: %s at %s %n", instanceName, zone);

            // Insert the instance in the specified project and zone.
            InsertInstanceRequest insertInstanceRequest = InsertInstanceRequest.newBuilder()
                    .setProject(project)
                    .setZone(zone)
                    .setInstanceResource(instanceResource)
                    .build();

            OperationFuture<Operation, Operation> operation = instancesClient.insertAsync(insertInstanceRequest);

            // Wait for the operation to complete.
            Operation response = operation.get(3, TimeUnit.MINUTES);

            if (response.hasError()) {
                System.out.println("Instance creation failed ! ! " + response);
                return;
            }
            System.out.println("Operation Status: " + response.getStatus());
        }
    }

    // Delete the instance specified by `instanceName`
    // if it's present in the given project and zone.
    public static void deleteInstance(String project, String zone, String instanceName)
            throws IOException, InterruptedException, ExecutionException, TimeoutException {
        // Initialize client that will be used to send requests. This client only needs to be created
        // once, and can be reused for multiple requests. After completing all of your requests, call
        // the `instancesClient.close()` method on the client to safely
        // clean up any remaining background resources.
        try (InstancesClient instancesClient = InstancesClient.create()) {
            System.out.printf("Deleting instance: %s ", instanceName);

            // Describe which instance is to be deleted.
            DeleteInstanceRequest deleteInstanceRequest = DeleteInstanceRequest.newBuilder()
                    .setProject(project)
                    .setZone(zone)
                    .setInstance(instanceName).build();

            OperationFuture<Operation, Operation> operation = instancesClient.deleteAsync(deleteInstanceRequest);
            // Wait for the operation to complete.
            Operation response = operation.get(3, TimeUnit.MINUTES);

            if (response.hasError()) {
                System.out.println("Instance deletion failed ! ! " + response);
                return;
            }
            System.out.println("Operation Status: " + response.getStatus());
        }
    }
}
