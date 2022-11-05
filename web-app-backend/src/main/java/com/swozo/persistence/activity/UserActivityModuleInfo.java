package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import com.swozo.persistence.user.User;
import com.swozo.utils.SupportedLanguage;
import lombok.*;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Entity
@Table(name = "UserActivityModuleInfos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UserActivityModuleInfo extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "activity_module_schedule_info_id")
    @ToString.Exclude
    private ActivityModuleScheduleInfo activityModuleScheduleInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "remote_file_id")
    private RemoteFile userFile;

    private String url;

    @OneToMany(mappedBy = "userActivityModuleInfo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @MapKey(name = "translationId.language")
    private Map<SupportedLanguage, TranslatableActivityLink> translations = new HashMap<>();

    public void setTranslation(TranslatableActivityLink translation) {
        translation.setUserActivityModuleInfo(this);
        this.translations.put(translation.getTranslationId().getLanguage(), translation);
    }

    public Optional<String> getUrl() {
        return Optional.ofNullable(url);
    }

    public Optional<RemoteFile> getFile() {
        return Optional.ofNullable(userFile);
    }
}
