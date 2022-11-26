package com.swozo.orchestrator.cloud.software.runner;

import com.swozo.orchestrator.configuration.ApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PlaybookPathProvider {
    private static final String ADMINISTRATION = "/administration";
    private static final String SOFTWARE = "/software";
    private static final String JUPYTER = "/jupyter";
    private static final String QUIZ_APP = "/quiz-app";
    private static final String SOZISEL = "/sozisel";
    private static final String DOCKER = "/docker";

    private final Map<Playbook, String> paths;

    @Autowired
    public PlaybookPathProvider(ApplicationProperties properties) {
        paths = Map.of(
                Playbook.EXECUTE_COMMAND, properties.ansibleHome() + ADMINISTRATION + "/execute-command.yml",
                Playbook.UPLOAD_TO_BUCKET, properties.ansibleHome() + ADMINISTRATION + "/upload-to-bucket.yml",
                Playbook.PROVISION_JUPYTER, properties.ansibleHome() + SOFTWARE + JUPYTER + "/prepare-and-run-jupyter.yml",
                Playbook.PREPARE_QUIZ_APP, properties.ansibleHome() + SOFTWARE + QUIZ_APP + "/prepare-quiz-app.yml",
                Playbook.RUN_QUIZ_APP, properties.ansibleHome() + SOFTWARE + QUIZ_APP + "/run-quiz-app.yml",
                Playbook.PROVISION_SOZISEL, properties.ansibleHome() + SOFTWARE + SOZISEL + "/prepare-and-run-sozisel.yml",
                Playbook.PREPARE_CUSTOM_DOCKER, properties.ansibleHome() + SOFTWARE + DOCKER + "/prepare-custom-docker.yml",
                Playbook.RUN_CUSTOM_DOCKER, properties.ansibleHome() + SOFTWARE + DOCKER + "/run-custom-docker.yml"
        );
    }

    public String getPlaybookPath(Playbook playbook) {
        return paths.get(playbook);
    }
}
