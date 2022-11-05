package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;

@Entity
@Table(name = "ActivityScheduleRequests")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityModuleScheduleInfo extends BaseEntity {
    Long scheduleRequestId;

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "activity_module_id")
    @ToString.Exclude
    private ActivityModule activityModule;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_module_schedule_info_id")
    @ToString.Exclude
    Collection<UserActivityLink> userActivityLinks = new LinkedList<>();

    public void addUserActivityLink(UserActivityLink userActivityLink) {
        userActivityLinks.add(userActivityLink);
    }
}
