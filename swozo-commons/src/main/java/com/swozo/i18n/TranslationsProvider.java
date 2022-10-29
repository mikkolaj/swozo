package com.swozo.i18n;

import com.swozo.utils.SupportedLanguage;

import java.util.Map;

public interface TranslationsProvider {
    String DEFAULT_VALUE = "";

    Map<SupportedLanguage, String> getTranslations(String key, Map <String, String> params);

    default Map<SupportedLanguage, String> t(String key, Map<String, String> params) {
        return getTranslations(key, params);
    }

    default Map<SupportedLanguage, String> t(String key) {
        return getTranslations(key, Map.of());
    }
}
