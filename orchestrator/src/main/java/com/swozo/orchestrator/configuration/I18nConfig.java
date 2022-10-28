package com.swozo.orchestrator.configuration;

import com.swozo.i18n.JsonFilesTranslationsProvider;
import com.swozo.i18n.TranslationsProvider;
import com.swozo.utils.SupportedLanguage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class I18nConfig {
    private final static String TRANSLATIONS_DIR = "translations";

    @Bean
    public TranslationsProvider provideTranslationsProvider() throws URISyntaxException, IOException {
        return new JsonFilesTranslationsProvider()
                .withSupportFor(SupportedLanguage.PL, getPathToTranslationsFile(SupportedLanguage.PL));
    }

    private Path getPathToTranslationsFile(SupportedLanguage language) throws URISyntaxException {
        return Paths.get(getClass().getClassLoader()
                .getResource(Path.of(TRANSLATIONS_DIR, language.toString().toLowerCase() + ".json").toString())
                .toURI()
        );
    }
}
