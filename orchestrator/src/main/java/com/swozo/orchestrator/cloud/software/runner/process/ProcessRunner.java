package com.swozo.orchestrator.cloud.software.runner.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final int SUCCESS_CODE = 0;
    public Process createProcess(String command) throws ProcessFailed {
        try {
            logger.debug("Running command: {}", command);
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ProcessFailed(e);
        }
    }

    public Process createProcess(String[] command) throws ProcessFailed {
        try {
            if (logger.isDebugEnabled()) logger.debug("Running command: {}", Arrays.toString(command));
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ProcessFailed(e);
        }
    }

    public void waitFor(Process process, int timeoutMinutes) throws ProcessFailed, InterruptedException {
        var success = process.waitFor(timeoutMinutes, TimeUnit.MINUTES);
        if (!success) {
            process.destroyForcibly();
            throw new ProcessFailed(String.format("Process: \"%s\" timed out.", process));
        }
    }
}
