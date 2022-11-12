package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface VmMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    VmEntity toPersistence(VmAddress dto);

    VmAddress toDto(VmEntity entity);
}
