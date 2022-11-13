package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;

@Entity
@Table(name = "ActivityModuleScheduleInfo")
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

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "activityModuleScheduleInfo")
    @ToString.Exclude
    Collection<UserActivityModuleInfo> userActivityModuleData = new LinkedList<>();

    public void addUserActivityLink(UserActivityModuleInfo userActivityModuleInfo) {
        userActivityModuleInfo.setActivityModuleScheduleInfo(this);
        this.userActivityModuleData.add(userActivityModuleInfo);
    }
}
