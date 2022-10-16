package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.properties.FieldType;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class JupyterParameters {
    private static final String NOTEBOOK_LOCATION_PARAM = "notebookLocation";
    private final String notebookLocation;

    public static JupyterParameters from(Map<String, String> dynamicParameters) throws InvalidParametersException {
        var notebookLocation = Optional.ofNullable(dynamicParameters.get(NOTEBOOK_LOCATION_PARAM))
                .orElseThrow(() -> new InvalidParametersException("Parameter \"notebookLocation\" must be present"));
        return new JupyterParameters(notebookLocation);
    }

    public static List<ParameterDescription> getParameterDescriptions() {
        return List.of(new ParameterDescription(NOTEBOOK_LOCATION_PARAM, true, FieldType.FILE));
    }
}
