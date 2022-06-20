package com.swozo.orchestrator.cloud.software.runner;

import com.swozo.orchestrator.cloud.software.runner.process.ProcessFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessRunner;
import com.swozo.orchestrator.cloud.software.ssh.SshService;
import com.swozo.orchestrator.cloud.software.ssh.SshTarget;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

@Service
@RequiredArgsConstructor
public class AnsibleRunner {
    private static final String PLAYBOOK_COMMAND = "ansible-playbook";
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


    public void runPlaybook(
            List<SshTarget> sshTargets,
            String sshUser,
            String sshKeyPath,
            String playbookPath,
            int timeoutMinutes
    ) throws InterruptedException, NotebookFailed {
        runPlaybook(sshTargets, sshUser, sshKeyPath, playbookPath, Collections.emptyList(), timeoutMinutes);
    }

    public void runPlaybook(
            List<SshTarget> sshTargets,
            String sshUser,
            String sshKeyPath,
            String playbookPath,
            List<String> userVars,
            int timeoutMinutes
    ) throws InterruptedException, NotebookFailed {
        try {
            sshTargets.forEach(this::waitForConnection);
            var command = createAnsibleCommand(sshTargets, sshKeyPath, sshUser, playbookPath, userVars);
            sshService.clearAllSshHostEntries(sshTargets);
            var process = processRunner.createProcess(command);
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
        } catch (ProcessFailed e) {
            throw new NotebookFailed(e);
        }
    }

    private void waitForConnection(SshTarget target) {
        sshService.waitForConnection(target, DEFAULT_CONNECTION_ATTEMPTS);
        logger.info("Connection successful: {}", target);
    }

    private String[] createAnsibleCommand(
            List<SshTarget> hosts,
            String sshKeyPath,
            String sshUser,
            String playbookPath,
            List<String> userVars
    ) {
        var hostParams = hosts.stream().map(SshTarget::toString).toList();
        var inventory = String.join(INVENTORY_DELIMITER, hostParams) + INVENTORY_DELIMITER;
        var extraVarsArgument = buildExtraVarsArgument(sshUser, userVars);

        return new String[]{
                PLAYBOOK_COMMAND,
                INVENTORY_ARG_NAME,
                inventory,
                PRIVATE_KEY_ARG_NAME,
                sshKeyPath,
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
