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

    private final Map<Playbook, String> paths;

    @Autowired
    public PlaybookPathProvider(ApplicationProperties properties) {
        paths = Map.of(
                Playbook.EXECUTE_COMMAND, properties.ansibleHome() + ADMINISTRATION + "/execute-command.yml",
                Playbook.COPY_FILES, properties.ansibleHome() + ADMINISTRATION + "/copy-files.yml",
                Playbook.UPLOAD_TO_BUCKET, properties.ansibleHome() + ADMINISTRATION + "/upload-to-bucket.yml",
                Playbook.PROVISION_JUPYTER, properties.ansibleHome() + SOFTWARE + JUPYTER + "/prepare-and-run-jupyter.yml",
                Playbook.PREPARE_QUIZ_APP, properties.ansibleHome() + SOFTWARE + QUIZ_APP + "/prepare-quiz-app.yml",
                Playbook.RUN_QUIZ_APP, properties.ansibleHome() + SOFTWARE + QUIZ_APP + "/run-quiz-app.yml"
        );
    }


    public String getPlaybookPath(Playbook playbook) {
        return paths.get(playbook);
    }
}
