package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.servicemodule.ServiceModule;
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
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "service_module_id")
    private ServiceModule serviceModule;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "activityModule")
    @ToString.Exclude
    private Collection<ActivityModuleScheduleInfo> schedules = new ArrayList<>();

    public ActivityModule(ServiceModule serviceModule) {
        this.serviceModule = serviceModule;
    }

    public void addScheduleInfo(ActivityModuleScheduleInfo scheduleInfo) {
        this.schedules.add(scheduleInfo);
        scheduleInfo.setActivityModule(this);
    }


//    public void addLink(ActivityLink link) {
//        links.add(link);
//    }
//
//    public Optional<Long> getRequestId() {
//        return Optional.ofNullable(requestId);
//    }
}
