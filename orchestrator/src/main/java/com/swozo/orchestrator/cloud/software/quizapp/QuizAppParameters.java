package com.swozo.orchestrator.cloud.software.quizapp;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public record QuizAppParameters(int quizDurationSeconds, String questionsLocation) {
    private static final String QUESTIONS_LOCATION_PARAM = "questionsLocation";
    private static final String QUIZ_DURATION_SECONDS_PARAM = "quizDurationSeconds";

    public static QuizAppParameters from(Map<String, String> dynamicParameters) throws InvalidParametersException {
        var questionsLocation = getRequiredParam(dynamicParameters, QUESTIONS_LOCATION_PARAM);
        var quizDurationSeconds = Integer.valueOf(getRequiredParam(dynamicParameters, QUIZ_DURATION_SECONDS_PARAM));
        return new QuizAppParameters(quizDurationSeconds, questionsLocation);
    }

    public static List<ParameterDescription> getParameterDescriptions(TranslationsProvider translationsProvider) {
        return List.of(
                ParameterDescription.builder(QUESTIONS_LOCATION_PARAM)
                        .withTranslatedLabel(
                                translationsProvider.t("services.quizApp.dynamicParams.questionsLocation.label")
                        )
                        .ofFile()
                        .withAllowedExtensions(List.of("yaml", "yml"))
                        .build(),
                ParameterDescription.builder(QUIZ_DURATION_SECONDS_PARAM)
                        .withTranslatedLabel(
                                translationsProvider.t("services.quizApp.dynamicParams.quizDurationSeconds.label")
                        )
                        .ofText()
                        .build()
        );
    }

    private static String getRequiredParam(Map<String, String> dynamicParameters, String name) {
        return Optional.ofNullable(dynamicParameters.get(name))
                .orElseThrow(() -> new InvalidParametersException("Parameter " + name + " must be present"));
    }
}
