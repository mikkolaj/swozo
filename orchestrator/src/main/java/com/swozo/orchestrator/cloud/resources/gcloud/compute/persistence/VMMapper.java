package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface VMMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    VMEntity toPersistence(VMAddress dto);

    VMAddress toDto(VMEntity entity);
}
