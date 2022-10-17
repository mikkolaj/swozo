package com.swozo.mapper;

import com.swozo.api.common.files.dto.FileDto;
import com.swozo.api.common.files.dto.UploadAccessDto;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.persistence.RemoteFile;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class FileMapper {
    @Autowired
    protected FilePathProvider filePathProvider;

    @Mapping(target = "name", expression = "java(filePathProvider.getFilename(file.getPath()))")
    public abstract FileDto toDto(RemoteFile file);

    @Mapping(target = "path", expression = "java(uploadAccessDto.storageAccessRequest().filePath())")
    @Mapping(target = "sizeBytes", expression = "java(uploadAccessDto.initFileUploadRequest().sizeBytes())")
    public abstract RemoteFile toPersistence(UploadAccessDto uploadAccessDto);
}
