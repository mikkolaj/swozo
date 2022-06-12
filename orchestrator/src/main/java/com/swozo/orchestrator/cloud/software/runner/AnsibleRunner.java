package com.swozo.orchestrator.cloud.software.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

@Service
public class AnsibleRunner {
    private static final String PLAYBOOK_COMMAND = "ansible-playbook";
    private static final String INVENTORY_ARG_NAME = "-i";
    private static final String PRIVATE_KEY_ARG_NAME = "--private-key";
    private static final String EXTRA_VARS_ARG_NAME = "--extra-vars";
    private static final String EXTRA_VARS_TEMPLATE = "ansible_ssh_user=%s";
    private static final int EXEC_ERROR_CODE = 123;
    private static final String INPUT_BOUNDARY = "\\A";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CommandResult runNotebook(List<SshTarget> sshTargets, String sshUser, String sshKeyPath, String playbookPath) throws InterruptedException {
        try {
            var command = createAnsibleCommand(sshTargets, sshKeyPath, sshUser, playbookPath);
            var process = Runtime.getRuntime().exec(command);
            try (var outputScanner = new Scanner(process.getInputStream()).useDelimiter(INPUT_BOUNDARY);
                 var errorScanner = new Scanner(process.getErrorStream()).useDelimiter(INPUT_BOUNDARY)
            ) {
                var returnCode = process.waitFor();
                var output = outputScanner.hasNext() ? outputScanner.next() : "";
                var errors = errorScanner.hasNext() ? errorScanner.next() : "";

                return new CommandResult(returnCode, output, errors);
            }
        } catch (IOException exception) {
            logger.error(exception.getMessage());
            return new CommandResult(EXEC_ERROR_CODE, "", "");
        }
    }

    private String[] createAnsibleCommand(List<SshTarget> hosts, String sshKeyPath, String sshUser, String playbookPath) {
        var hostParams = hosts.stream().map(SshTarget::toString).toList();
        var inventory = String.join(",", hostParams) + ",";
        var extraVars = String.format(EXTRA_VARS_TEMPLATE, sshUser);

        return new String[]{
                PLAYBOOK_COMMAND,
                INVENTORY_ARG_NAME,
                inventory,
                PRIVATE_KEY_ARG_NAME,
                sshKeyPath,
                EXTRA_VARS_ARG_NAME,
                extraVars,
                playbookPath
        };
    }

    // Local provisioning test
    public static void main(String[] args) throws InterruptedException {
        System.out.println(new AnsibleRunner().runNotebook(
                List.of(new SshTarget("localhost", 2222)),
                "vagrant",
                "/home/mikolaj/IdeaProjects/swozo/orchestrator/src/main/resources/provisioning/local/.vagrant/machines/default/virtualbox/private_key",
                "/home/mikolaj/IdeaProjects/swozo/orchestrator/src/main/resources/provisioning/software/jupyter/prepare-and-run-jupyter.yml"
        ));
    }
}
