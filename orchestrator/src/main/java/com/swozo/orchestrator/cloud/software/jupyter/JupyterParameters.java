package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record JupyterParameters(String notebookLocation) {
    private static final String NOTEBOOK_LOCATION_PARAM = "notebookLocation";

    public static JupyterParameters from(Map<String, String> dynamicParameters) throws InvalidParametersException {
        var notebookLocation = Optional.ofNullable(dynamicParameters.get(NOTEBOOK_LOCATION_PARAM))
                .orElseThrow(() -> new InvalidParametersException("Parameter \"notebookLocation\" must be present"));
        return new JupyterParameters(notebookLocation);
    }

    public static List<ParameterDescription> getParameterDescriptions(TranslationsProvider translationsProvider) {
        return List.of(
                ParameterDescription.builder(NOTEBOOK_LOCATION_PARAM)
                        .withTranslatedLabel(
                            translationsProvider.t("services.jupyter.dynamicParams.notebookLocation.label")
                        )
                        .ofFile()
                        .withAllowedExtensions(List.of("ipynb"))
                        .build()
        );
    }
}
