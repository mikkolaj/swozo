package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.user.User;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "ActivityModules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityModule extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "service_module_id")
    private ServiceModule serviceModule;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "activityModule")
    @ToString.Exclude
    private Collection<ActivityModuleScheduleInfo> schedules = new ArrayList<>();

    private Boolean linkConfirmationRequired = false;
    private Boolean linkConfirmed = false;

    public ActivityModule(ServiceModule serviceModule, boolean linkConfirmationRequired) {
        this.serviceModule = serviceModule;
        this.linkConfirmationRequired = linkConfirmationRequired;
    }

    public void addScheduleInfo(ActivityModuleScheduleInfo scheduleInfo) {
        this.schedules.add(scheduleInfo);
        scheduleInfo.setActivityModule(this);
    }

    public boolean isLinkConfirmationRequired() {
        return linkConfirmationRequired;
    }

    public boolean isLinkConfirmed() {
        return linkConfirmed;
    }

    public boolean canStudentReceiveLink() {
        return linkConfirmed || !linkConfirmationRequired;
    }

    public User getTeacher() {
        return activity.getTeacher();
    }
}
