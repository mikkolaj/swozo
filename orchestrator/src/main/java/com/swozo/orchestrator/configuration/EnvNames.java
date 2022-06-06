package com.swozo.orchestrator.configuration;

public class EnvNames {

    private EnvNames() {
    }

    public static final String SCHEDULER_THREAD_POOL_SIZE = "scheduler.thread.pool.size";
    public static final String GCP_PROJECT = "gcp.project";
    public static final String GCP_ZONE = "gcp.zone";
    public static final String GCP_COMPUTE_IMAGE_FAMILY = "gcp.compute.image-family";
    public static final String GCP_REQUEST_TIMEOUT = "gcp.request-timeout";
    public static final String GCP_SSH_USER = "gcp.ssh.user";
    public static final String GCP_SSH_KEY_PATH = "gcp.ssh.key-path";
}
