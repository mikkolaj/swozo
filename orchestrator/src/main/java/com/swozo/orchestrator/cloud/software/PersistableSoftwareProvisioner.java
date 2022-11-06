package com.swozo.orchestrator.cloud.software;

public interface PersistableSoftwareProvisioner extends TimedSoftwareProvisioner {
    String getWorkdirToSave();

    int getCleanupSeconds();
}
