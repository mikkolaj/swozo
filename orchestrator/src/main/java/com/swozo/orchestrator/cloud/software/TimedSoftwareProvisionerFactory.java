package com.swozo.orchestrator.cloud.software;

import com.swozo.model.scheduling.properties.ScheduleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimedSoftwareProvisionerFactory {
    private final Map<ScheduleType, TimedSoftwareProvisioner> provisioners;

    @Autowired
    public TimedSoftwareProvisionerFactory(List<TimedSoftwareProvisioner> provisioners) {
        this.provisioners = provisioners.stream()
                .collect(Collectors.toMap(TimedSoftwareProvisioner::getScheduleType, p -> p));
    }

    public TimedSoftwareProvisioner getProvisioner(ScheduleType type) {
        return Optional.ofNullable(provisioners.get(type))
                .orElseThrow(() -> new IllegalStateException(String.format("Provisioner for type: %s is not yet implemented!", type)));
    }

    public List<TimedSoftwareProvisioner> getAllProvisioners() {
        return provisioners.values().stream().toList();
    }
}
