package com.swozo.orchestrator.cloud.software.ssh;

import com.swozo.exceptions.ConnectionFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessRunner;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import com.swozo.utils.CheckedExceptionConverter;
import com.swozo.utils.RetryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SshService {
    private static final String CLEAR_SSH_ENTRY_TEMPLATE = "ssh-keygen -R %s";
    private static final String TIMEOUT_COMMAND = "timeout";
    private static final String DEFAULT_TIMEOUT_SECONDS = "1";
    private static final String BASH_COMMAND = "bash";
    private static final String BASH_COMMAND_ARGUMENT = "-c";
    private static final String TEST_CONNECTION_TEMPLATE = "</dev/tcp/%s/%s";
    private final ApplicationProperties properties;
    private final ProcessRunner processRunner;

    public void waitForConnection(SshTarget target, int attempts) throws ConnectionFailed {
        CheckedExceptionConverter.from(() -> RetryHandler.retryExponentially(
                () -> testConnection(target), attempts), ConnectionFailed::new
        ).run();
    }

    public void clearAllSshHostEntries(Collection<SshTarget> sshTargets) throws ConnectionFailed {
        sshTargets.forEach(CheckedExceptionConverter.from(this::clearSshHostEntry));
    }

    public void clearSshHostEntry(SshTarget sshTarget) throws InterruptedException, ConnectionFailed {
        try {
            var clearEntryCommand = String.format(CLEAR_SSH_ENTRY_TEMPLATE, sshTarget.ipAddress());
            var process = processRunner.createProcess(clearEntryCommand);
            processRunner.waitFor(process, properties.systemCommandTimeoutMinutes());
        } catch (ProcessFailed e) {
            throw new ConnectionFailed(e);
        }
    }

    private String[] buildConnectionTestCommand(SshTarget target) {
        return new String[]{
                TIMEOUT_COMMAND,
                DEFAULT_TIMEOUT_SECONDS,
                BASH_COMMAND,
                BASH_COMMAND_ARGUMENT,
                String.format(TEST_CONNECTION_TEMPLATE, target.ipAddress(), target.sshPort())
        };
    }

    private void testConnection(SshTarget target) throws InterruptedException {
        var command = buildConnectionTestCommand(target);
        var process = processRunner.createProcess(command);
        processRunner.waitFor(process, properties.systemCommandTimeoutMinutes());

        if (process.exitValue() != ProcessRunner.SUCCESS_CODE) {
            throw new ConnectionFailed(String.format("Failed to reach: %s", target));
        }
    }
}
