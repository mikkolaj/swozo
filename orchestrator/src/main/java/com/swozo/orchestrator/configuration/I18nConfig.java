package com.swozo.orchestrator.configuration;

import com.swozo.i18n.JsonFilesTranslationsProvider;
import com.swozo.i18n.TranslationsProvider;
import com.swozo.utils.SupportedLanguage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

@Configuration
public class I18nConfig {
    private final static String TRANSLATIONS_DIR = "translations";

    @Bean
    public TranslationsProvider provideTranslationsProvider() throws IOException {
        return new JsonFilesTranslationsProvider()
                .withSupportFor(SupportedLanguage.PL, getJsonTranslations(SupportedLanguage.PL));
    }

    private String getJsonTranslations(SupportedLanguage language) throws IOException {
        var path = Path.of(TRANSLATIONS_DIR, language.toString().toLowerCase() + ".json").toString();
        try (var inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            assert inputStream != null;
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
