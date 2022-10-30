package com.swozo.i18n;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.swozo.utils.SupportedLanguage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Simplified java implementation of react-i18n, see this for general idea https://react.i18next.com/
 */
public class JsonFilesTranslationsProvider implements TranslationsProvider{
    // matches {{someVariable}}
    private final static Pattern PLACEHOLDER_REGEX = Pattern.compile("(\\{\\{([\\w\\d]+?)\\}\\})");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<SupportedLanguage, JsonNode> translations;

    public JsonFilesTranslationsProvider() {
        translations = new HashMap<>();
    }

    public JsonFilesTranslationsProvider withSupportFor(SupportedLanguage language, String jsonTranslations) throws IOException  {
        translations.put(language, readTranslations(jsonTranslations));
        return this;
    }

    @Override
    public Map<SupportedLanguage, String> getTranslations(String key, Map<String, String> params) {
        return translations.keySet().stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        language -> substitutePlaceholders(getByKey(language, key), params))
                );
    }

    private String getByKey(SupportedLanguage language, String key) {
        try {
            var pathElements = key.split("\\.");
            var curNode = translations.get(language);
            for (var pathElem : pathElements) {
                curNode = curNode.findPath(pathElem);
            }

            if (curNode.getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Can't extract text from node with type: " + curNode.getNodeType());
            }

            return curNode.asText();
        } catch (Exception ex) {
            logger.error(String.format("Failed to extract translation for path: '%s', for language: %s", key, language), ex);
            return DEFAULT_VALUE;
        }
    }

    private String substitutePlaceholders(String value, Map<String, String> placeholders) {
        return PLACEHOLDER_REGEX.matcher(value).replaceAll(matchResult -> {
            try {
                return Optional.ofNullable(placeholders.get(matchResult.group(2))).orElse(matchResult.group());
            } catch (Exception ex) {
                return DEFAULT_VALUE;
            }
        });
    }

    private JsonNode readTranslations(String jsonTranslations) throws JsonProcessingException {
        return new ObjectMapper().readTree(jsonTranslations);
    }
}
