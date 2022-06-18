package com.swozo.orchestrator.cloud.software.runner.process;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
public class ProcessRunner {
    public static final int SUCCESS_CODE = 0;
    public Process createProcess(String command) throws ProcessFailed {
        try {
            return Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new ProcessFailed(e);
        }
    }

    public Process createProcess(String[] command) throws ProcessFailed {
        try {
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
