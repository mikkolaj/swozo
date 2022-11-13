package com.swozo.persistence.activity.utils;

import com.swozo.persistence.activity.UserActivityModuleInfo;
import com.swozo.utils.SupportedLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "TranslatableInstruction")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TranslatableActivityLink {
    @EmbeddedId
    private TranslationId translationId;

    @ManyToOne
    @MapsId("id")
    @JoinColumn(name = "id")
    @ToString.Exclude
    private UserActivityModuleInfo userActivityModuleInfo;

    private String instructionHtml;

    public TranslatableActivityLink(SupportedLanguage language, String instructionHtml) {
        this.translationId = new TranslationId(language);
        this.instructionHtml = instructionHtml;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslatableActivityLink that)) return false;
        return Objects.equals(translationId, that.translationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(translationId);
    }
}
