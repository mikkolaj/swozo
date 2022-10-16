package com.swozo.model.scheduling;

import com.swozo.model.scheduling.properties.FieldType;
import com.swozo.utils.SupportedLanguage;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record ParameterDescription(
        @Schema(required = true) String name,
        @Schema(required = true) boolean required,
        @Schema(required = true) FieldType type,
        Optional<Map<SupportedLanguage, String>> translatedLabel,
        Optional<Map<String, Object>> clientValidationHelpers
) {

    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static Builder builder(String name, boolean required) {
        return new Builder(name, required);
    }

    public static class Builder {
        private final FieldType fieldType;
        private final String name;
        private final boolean required;
        protected Map<SupportedLanguage, String> translatedLabel;
        protected final Map<String, Object> validationHelpers;

        protected Builder(String name, boolean required, FieldType fieldType, Map<SupportedLanguage, String> translatedLabel) {
            this.name = name;
            this.required = required;
            this.fieldType = fieldType;
            this.translatedLabel = translatedLabel;
            this.validationHelpers = new HashMap<>();
        }

        public Builder(String name, boolean required) {
            this(name, required, null, null);
        }

        public Builder(String name) {
            this(name, true);
        }

        protected ParameterDescription build() {
            return new ParameterDescription(
                    name,
                    required,
                    fieldType,
                    Optional.ofNullable(translatedLabel),
                    validationHelpers.isEmpty() ? Optional.empty() : Optional.of(validationHelpers)
            );
        }

        public Builder withTranslatedLabel(Map<SupportedLanguage, String> translatedLabel) {
            this.translatedLabel = translatedLabel;
            return this;
        }

        public FileParameterBuilder ofFile() {
            return new FileParameterBuilder(name, required, translatedLabel);
        }

        public TextParameterBuilder ofText() {
            return new TextParameterBuilder(name, required, translatedLabel);
        }
    }

    public static class FileParameterBuilder extends Builder {
        private static final String ALLOWED_EXTENSIONS = "allowedExtensions";

        public FileParameterBuilder(String name, boolean required, Map<SupportedLanguage, String> translatedLabel) {
            super(name, required, FieldType.FILE, translatedLabel);
        }

        public FileParameterBuilder withAllowedExtensions(List<String> extensions) {
            validationHelpers.put(ALLOWED_EXTENSIONS, extensions);
            return this;
        }

        @Override
        public ParameterDescription build() {
            return super.build();
        }
    }

    public static class TextParameterBuilder extends Builder {
        protected TextParameterBuilder(String name, boolean required, Map<SupportedLanguage, String> translatedLabel) {
            super(name, required, FieldType.TEXT, translatedLabel);
        }

        @Override
        public ParameterDescription build() {
            return super.build();
        }
    }
}
