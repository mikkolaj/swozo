package com.swozo.api.web.servicemodule.dynamic.fields;

import com.swozo.api.common.files.FileService;
import com.swozo.api.common.files.dto.UploadAccessDto;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.servicemodule.request.FinishServiceModuleCreationRequest;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.mapper.FileMapper;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.properties.FieldType;
import com.swozo.model.utils.StorageAccessRequest;
import com.swozo.persistence.servicemodule.ServiceModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FileFieldHandler implements DynamicFieldHandler {
    private final FileService fileService;
    private final FileMapper fileMapper;
    private final FilePathProvider filePathProvider;
    private final JsonMapperFacade mapper;

    @Override
    public FieldType getType() {
        return FieldType.FILE;
    }

    @Override
    public Optional<Object> handleForServiceModuleReservation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            ParameterDescription parameterDescription
    ) {
        return Optional.of(
                fileService.prepareExternalUpload(
                    mapper.fromJson(request.dynamicProperties().get(fieldName), InitFileUploadRequest.class),
                    filePathProvider.serviceModuleFilePath(serviceModuleReservation),
                    () -> {}
                )
        );
    }

    @Override
    public String handleForServiceModuleCreation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            FinishServiceModuleCreationRequest request,
            ParameterDescription parameterDescription
    ) {
        var file = fileService.acknowledgeExternalUploadWithoutTxn(
                serviceModuleReservation.getCreator(),
                new UploadAccessDto(
                    mapper.fromJson(request.repeatedInitialValues().get(fieldName), InitFileUploadRequest.class),
                    mapper.fromJson(request.echoFieldActions().get(fieldName), StorageAccessRequest.class)
                )
        );

        return fileService.encodeUniqueIdentifier(file);
    }

    @Override
    public String decodeValue(String storedValue, ParameterDescription parameterDescription) {
        return mapper.toJson(fileMapper.toDto(fileService.decodeUniqueIdentifier(storedValue)));
    }

    @Override
    public void cleanup(String storedValue, ParameterDescription parameterDescription) {
        var file = fileService.decodeUniqueIdentifier(storedValue);
        fileService.removeFileInternally(file);
    }
}
