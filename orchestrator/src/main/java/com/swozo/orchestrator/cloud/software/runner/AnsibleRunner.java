package com.swozo.orchestrator.cloud.software.runner;

import com.swozo.orchestrator.cloud.software.runner.process.ProcessFailed;
import com.swozo.orchestrator.cloud.software.runner.process.ProcessRunner;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import com.swozo.utils.CheckedExceptionConverter;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ApplicationProperties properties;
    private final ProcessRunner processRunner;


    public void runNotebook(
            List<SshTarget> sshTargets,
            String sshUser,
            String sshKeyPath,
            String playbookPath,
            int timeoutMinutes
    ) throws InterruptedException, NotebookFailed {
        runNotebook(sshTargets, sshUser, sshKeyPath, playbookPath, Collections.emptyList(), timeoutMinutes);
    }

    public void runNotebook(
            List<SshTarget> sshTargets,
            String sshUser,
            String sshKeyPath,
            String playbookPath,
            List<String> userVars,
            int timeoutMinutes
    ) throws InterruptedException, NotebookFailed {
        try {
            var command = createAnsibleCommand(sshTargets, sshKeyPath, sshUser, playbookPath, userVars);
            clearAllHostEntries(sshTargets);
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
                }
            }
        } catch (ProcessFailed e) {
            throw new NotebookFailed(e);
        }
    }

    private void clearAllHostEntries(List<SshTarget> sshTargets) {
        sshTargets.stream()
                .map(SshTarget::ipAddress)
                .forEach(CheckedExceptionConverter.from(this::clearSshHostEntries));
    }

    private void clearSshHostEntries(String ip) throws InterruptedException, NotebookFailed {
        try {
            var process = processRunner.createProcess(String.format("ssh-keygen -R %s", ip));
            processRunner.waitFor(process, properties.systemCommandTimeoutMinutes());
        } catch (ProcessFailed e) {
            throw new NotebookFailed(e);
        }
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
        var extraVars = getAllVars(sshUser, userVars);
        var extraVarsArgument = String.join(EXTRA_VARS_DELIMITER, extraVars);

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

    private List<String> getAllVars(String sshUser, List<String> userVars) {
        var sshUserArgument = String.format(SSH_USER_TEMPLATE, sshUser);
        var vars = new ArrayList<>(List.of(sshUserArgument, DISABLE_STRICT_HOST_CHECKING));
        vars.addAll(userVars);
        return Collections.unmodifiableList(vars);
    }
}
