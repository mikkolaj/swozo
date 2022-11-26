package com.swozo.orchestrator.cloud.software.docker;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.utils.SupportedLanguage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record DockerParameters(
        String publicImageName,
        int expectedServiceStartupSeconds,
        long imageSizeBytes,
        int portToExpose,
        Optional<String> resultsPathInContainer,
        Optional<String> startEndpoint,
        Optional<InputFile> inputFile
) {
    private static final String PUBLIC_IMAGE_NAME = "publicImageName";
    private static final String EXPECTED_SERVICE_STARTUP_SECONDS = "expectedServiceStartupSeconds";
    private static final String IMAGE_SIZE_BYTES = "imageSizeBytes";
    private static final String PORT_TO_EXPOSE = "portToExpose";
    private static final String RESULTS_PATH_IN_CONTAINER = "resultsPathInContainer";
    private static final String START_ENDPOINT = "startEndpoint";
    private static final String INPUT_FILE_LOCATION = "inputFileLocation";
    private static final String CONTAINER_INPUT_FILE_PATH = "containerInputFilePath";

    public static DockerParameters from(Map<String, String> dynamicParameters) throws InvalidParametersException {
        return new DockerParameters(
                getRequiredParam(dynamicParameters, PUBLIC_IMAGE_NAME),
                Integer.parseInt(getRequiredParam(dynamicParameters, EXPECTED_SERVICE_STARTUP_SECONDS)),
                Long.parseLong(getRequiredParam(dynamicParameters, IMAGE_SIZE_BYTES)),
                parsePort(getRequiredParam(dynamicParameters, PORT_TO_EXPOSE)),
                Optional.ofNullable(dynamicParameters.get(RESULTS_PATH_IN_CONTAINER)),
                Optional.ofNullable(dynamicParameters.get(START_ENDPOINT)),
                Optional.ofNullable(dynamicParameters.get(INPUT_FILE_LOCATION))
                        .map(fileLocation -> new InputFile(fileLocation, getRequiredParam(dynamicParameters, CONTAINER_INPUT_FILE_PATH)))
            );
    }

    private static int parsePort(String portString) {
        var port = Integer.parseInt(portString);
        if (port < 0 || port > 65535)
            throw new IllegalArgumentException("Invalid port " + port);
        return port;
    }

    public static List<ParameterDescription> getParameterDescriptions(TranslationsProvider translationsProvider) {
        return List.of(
                ParameterDescription.builder(PUBLIC_IMAGE_NAME)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.publicImageName.label")
                        )
                        .ofText()
                        .build(),
                ParameterDescription.builder(EXPECTED_SERVICE_STARTUP_SECONDS)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.expectedServiceStartupSeconds.label")
                        )
                        .ofText()
                        .build(),
                ParameterDescription.builder(IMAGE_SIZE_BYTES)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.imageSizeBytes.label")
                        )
                        .ofText()
                        .build(),
                ParameterDescription.builder(PORT_TO_EXPOSE)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.portToExpose.label")
                        )
                        .ofText()
                        .build(),
                ParameterDescription.builder(RESULTS_PATH_IN_CONTAINER, false)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.resultPathInContainer.label")
                        )
                        .ofText()
                        .build(),
                ParameterDescription.builder(START_ENDPOINT, false)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.startEndpoint.label")
                        )
                        .ofText()
                        .build(),
                ParameterDescription.builder(INPUT_FILE_LOCATION, false)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.inputFileLocation.label")
                        )
                        .ofFile()
                        .build(),
                ParameterDescription.builder(CONTAINER_INPUT_FILE_PATH, false)
                        .withTranslatedLabel(
                                translationsProvider.t("services.docker.dynamicParams.containerInputFilePath.label")
                        )
                        .ofText()
                        .build()
        );
    }

    private static String getRequiredParam(Map<String, String> dynamicParameters, String name) {
        return Optional.ofNullable(dynamicParameters.get(name))
                .orElseThrow(() -> new InvalidParametersException("Parameter " + name + " must be present"));
    }

    public static Map<SupportedLanguage, String> getConfigurationInstruction(TranslationsProvider translationsProvider) {
        return translationsProvider.t("services.docker.instructions.configuration");
    }

    public static Map<SupportedLanguage, String> getUsageInstruction(TranslationsProvider translationsProvider) {
        return translationsProvider.t("services.docker.instructions.usage");
    }

    public record InputFile(String fileLocation, String containerFileLocationPath) {}
}
