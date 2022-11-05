package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import com.swozo.persistence.user.User;
import com.swozo.utils.SupportedLanguage;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "UserActivityLinks")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserActivityLink extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "activity_module_schedule_info_id")
    @ToString.Exclude
    private ActivityModuleScheduleInfo activityModuleScheduleInfo;

    private String url;

    @OneToMany(mappedBy = "userActivityLink", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "translationId.language")
    private Map<SupportedLanguage, TranslatableActivityLink> translations = new HashMap<>();

    public void setTranslation(TranslatableActivityLink translation) {
        translation.setUserActivityLink(this);
        this.translations.put(translation.getTranslationId().getLanguage(), translation);
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }
}
