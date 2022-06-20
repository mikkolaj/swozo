package com.swozo.orchestrator.cloud.software.ssh;

import com.swozo.exceptions.ConnectionFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessRunner;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import com.swozo.utils.CheckedExceptionConverter;
import com.swozo.utils.RetryHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class SshService {
    private static final String CLEAR_SSH_ENTRY_TEMPLATE = "ssh-keygen -R %s";
    private static final int DEFAULT_TIMEOUT_MILLISECONDS = 1000;
    private final ApplicationProperties properties;
    private final ProcessRunner processRunner;

    // TODO: make it async, this way multiple subsequent calls might slow things down
    // explanation: https://github.com/mikkolaj/swozo/pull/13
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

    private void testConnection(SshTarget target) throws IOException {
        try (var socket = new Socket()) {
            socket.connect(new InetSocketAddress(target.ipAddress(), target.sshPort()), DEFAULT_TIMEOUT_MILLISECONDS);
        }
    }
}
