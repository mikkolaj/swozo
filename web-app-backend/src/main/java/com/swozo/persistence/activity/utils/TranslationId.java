package com.swozo.persistence.activity.utils;

import com.swozo.utils.SupportedLanguage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TranslationId implements Serializable {
    private Long id;
    private SupportedLanguage language;

    public TranslationId(SupportedLanguage language) {
        this.language = language;
    }
}
