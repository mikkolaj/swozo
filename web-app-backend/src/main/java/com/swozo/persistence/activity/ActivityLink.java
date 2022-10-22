package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import com.swozo.utils.SupportedLanguage;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "ActivityLinks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityLink extends BaseEntity {
    private String url;
    private String connectionInfo;

    @OneToMany(mappedBy = "activityLink", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "translationId.language")
    private Map<SupportedLanguage, TranslatableActivityLink> translations = new HashMap<>();

    public void setTranslation(TranslatableActivityLink translation) {
        translation.setActivityLink(this);
        this.translations.put(translation.getTranslationId().getLanguage(), translation);
    }
}
