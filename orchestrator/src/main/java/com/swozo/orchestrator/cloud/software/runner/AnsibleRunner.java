package com.swozo.orchestrator.cloud.software.runner;

import com.swozo.exceptions.ConnectionFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessRunner;
import com.swozo.orchestrator.cloud.software.ssh.SshService;
import com.swozo.orchestrator.cloud.software.ssh.SshTarget;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AnsibleRunner {
    private static final String INVENTORY_ARG_NAME = "-i";
    private static final String PRIVATE_KEY_ARG_NAME = "--private-key";
    private static final String EXTRA_VARS_ARG_NAME = "--extra-vars";
    private static final String SSH_USER_TEMPLATE = "ansible_ssh_user=%s";
    private static final String DISABLE_STRICT_HOST_CHECKING = "ansible_ssh_extra_args='-o StrictHostKeyChecking=no'";
    private static final String INVENTORY_DELIMITER = ",";
    private static final String EXTRA_VARS_DELIMITER = " ";
    private static final String INPUT_BOUNDARY = "\\A";
    private static final int DEFAULT_CONNECTION_ATTEMPTS = 6;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ProcessRunner processRunner;
    private final SshService sshService;
    private final ApplicationProperties properties;


    public void runPlaybook(
            AnsibleConnectionDetails connectionDetails,
            String playbookPath,
            int timeoutMinutes
    ) throws InterruptedException, NotebookFailed {
        runPlaybook(connectionDetails, playbookPath, Collections.emptyList(), timeoutMinutes);
    }

    public void runPlaybook(
            AnsibleConnectionDetails connectionDetails,
            String playbookPath,
            List<String> userVars,
            int timeoutMinutes
    ) throws InterruptedException, NotebookFailed {
        try {
            handleSshStartup(connectionDetails.targets());
            var process = createAnsibleProcess(connectionDetails, playbookPath, userVars);
            waitForResult(process, timeoutMinutes);
        } catch (ProcessFailed | ConnectionFailed e) {
            throw new NotebookFailed(e);
        }
    }

    private void handleSshStartup(Collection<SshTarget> sshTargets) throws ConnectionFailed {
        sshService.clearAllSshHostEntries(sshTargets);
        sshTargets.forEach(this::waitForConnection);
    }

    private Process createAnsibleProcess(AnsibleConnectionDetails connectionDetails, String playbookPath, List<String> userVars)
            throws ProcessFailed {
        var command = createAnsibleCommand(connectionDetails, playbookPath, userVars);
        return processRunner.createProcess(command);
    }

    private void waitForConnection(SshTarget target) throws ConnectionFailed {
        sshService.waitForConnection(target, DEFAULT_CONNECTION_ATTEMPTS);
        logger.info("Connection successful: {}", target);
    }

    private void waitForResult(Process process, int timeoutMinutes) throws InterruptedException, NotebookFailed {
        try (var outputScanner = new Scanner(process.getInputStream()).useDelimiter(INPUT_BOUNDARY);
             var errorScanner = new Scanner(process.getErrorStream()).useDelimiter(INPUT_BOUNDARY)
        ) {
            processRunner.waitFor(process, timeoutMinutes);
            var output = outputScanner.hasNext() ? outputScanner.next() : "";
            var errors = errorScanner.hasNext() ? errorScanner.next() : "";

            if (process.exitValue() != ProcessRunner.SUCCESS_CODE) {
                logger.info(output);
                logger.error(errors);
                throw new NotebookFailed(errors);
            }
        }
    }

    private String[] createAnsibleCommand(AnsibleConnectionDetails connectionDetails, String playbookPath, List<String> userVars) {
        var hostParams = connectionDetails.targets().stream().map(SshTarget::toString).toList();
        var inventory = String.join(INVENTORY_DELIMITER, hostParams) + INVENTORY_DELIMITER;
        var extraVarsArgument = buildExtraVarsArgument(connectionDetails.sshUser(), userVars);

        return new String[]{
                properties.ansiblePlaybookExecutablePath(),
                INVENTORY_ARG_NAME,
                inventory,
                PRIVATE_KEY_ARG_NAME,
                connectionDetails.sshKeyPath(),
                EXTRA_VARS_ARG_NAME,
                extraVarsArgument,
                playbookPath
        };
    }

    private String buildExtraVarsArgument(String sshUser, List<String> userVars) {
        var sshUserArgument = String.format(SSH_USER_TEMPLATE, sshUser);
        var extraVars = new ArrayList<>(List.of(sshUserArgument, DISABLE_STRICT_HOST_CHECKING));
        extraVars.addAll(userVars);
        return String.join(EXTRA_VARS_DELIMITER, extraVars);
    }

}
