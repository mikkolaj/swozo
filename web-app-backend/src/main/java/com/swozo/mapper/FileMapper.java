package com.swozo.mapper;

import com.swozo.api.common.files.dto.FileDto;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class FileMapper {
    @Autowired
    protected FilePathProvider filePathProvider;

    @Mapping(target = "name", expression = "java(filePathProvider.getFilename(file.getPath()))")
    @Mapping(target = "createdAt", source = "registeredAt")
    public abstract FileDto toDto(RemoteFile file);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "path", expression = "java(uploadAccessDto.storageAccessRequest().filePath())")
    @Mapping(target = "sizeBytes", expression = "java(uploadAccessDto.initFileUploadRequest().sizeBytes())")
    @Mapping(target = "owner", expression = "java(owner)")
    public abstract RemoteFile toPersistence(UploadAccessDto uploadAccessDto, User owner);
}
