package com.swozo.orchestrator.cloud.software.ssh;

import com.swozo.exceptions.ConnectionFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessRunner;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import com.swozo.utils.CheckedExceptionConverter;
import com.swozo.utils.RetryHandler;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.common.config.keys.FilePasswordProvider;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Service
public class SshService implements DisposableBean {
    private static final String CLEAR_SSH_ENTRY_TEMPLATE = "ssh-keygen -R %s";
    private static final String FILE_SIZE_COMMAND_TEMPLATE = "ls -l %s | cut -d\" \" -f5";
    private static final int DEFAULT_SSH_COMMAND_ATTEMPTS = 3;
    private static final int DEFAULT_TIMEOUT_MILLISECONDS = 1000;
    private final ApplicationProperties properties;
    private final ProcessRunner processRunner;
    private final SshClient client = SshClient.setUpDefaultClient();

    @Autowired
    public SshService(ApplicationProperties properties, ProcessRunner processRunner) {
        this.properties = properties;
        this.processRunner = processRunner;
        client.start();
    }

    // TODO: make it async, this way multiple subsequent calls might slow things down
    // explanation: https://github.com/mikkolaj/swozo/pull/13
    public void waitForConnection(SshTarget target, SshAuth auth, int attempts) throws ConnectionFailed {
        CheckedExceptionConverter.from(() -> RetryHandler.retryExponentially(
                () -> testConnection(target, auth), attempts), ConnectionFailed::new
        ).run();
    }

    public long getFileSize(SshTarget target, SshAuth auth, String filePath) throws ConnectionFailed {
        return CheckedExceptionConverter.from(() -> RetryHandler.retryExponentially(
                () -> tryReadingFileSize(target, auth, filePath), DEFAULT_SSH_COMMAND_ATTEMPTS),
                ConnectionFailed::new
        ).get();
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

    private void testConnection(SshTarget target, SshAuth auth) throws IOException, GeneralSecurityException {
        try (var session = client.connect(auth.sshUser(), target.ipAddress(), target.sshPort())
                .verify(DEFAULT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .getSession()
        ) {
            SecurityUtils.getKeyPairResourceParser()
                    .loadKeyPairs(null, Paths.get(auth.sshKeyPath()), FilePasswordProvider.EMPTY)
                    .forEach(session::addPublicKeyIdentity);
            session.auth().await(DEFAULT_TIMEOUT_MILLISECONDS);
        }
    }

    private long tryReadingFileSize(SshTarget target, SshAuth auth, String filePath) throws IOException, GeneralSecurityException {
        try (var session = client.connect(auth.sshUser(), target.ipAddress(), target.sshPort())
                .verify(DEFAULT_TIMEOUT_MILLISECONDS, TimeUnit.MILLISECONDS)
                .getSession()
        ) {
            SecurityUtils.getKeyPairResourceParser()
                    .loadKeyPairs(null, Paths.get(auth.sshKeyPath()), FilePasswordProvider.EMPTY)
                    .forEach(session::addPublicKeyIdentity);
            session.auth().await(DEFAULT_TIMEOUT_MILLISECONDS);
            return Long.parseLong(session.executeRemoteCommand(String.format(FILE_SIZE_COMMAND_TEMPLATE, filePath)).trim());
        }
    }

    @Override
    public void destroy() throws IOException {
        client.close();
    }
}
