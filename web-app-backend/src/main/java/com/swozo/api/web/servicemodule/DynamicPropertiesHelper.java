package com.swozo.api.web.servicemodule;

import com.swozo.api.common.files.FileService;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.common.files.request.StorageAccessRequest;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.persistence.ServiceModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DynamicPropertiesHelper {
    private final FileService fileService;
    private final FilePathProvider filePathProvider;
    private final JsonMapperFacade mapper;

    public Optional<Object> handleFieldForReservation(
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            String initialDynamicProperty,
            ParameterDescription parameterDescription
    ) {
        return Optional.ofNullable(
                dispatchFieldReservationHandlingByType(
                        serviceModuleReservation,
                        request,
                        initialDynamicProperty,
                        parameterDescription
                )
        );
    }

    private Object dispatchFieldReservationHandlingByType(
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            String initialDynamicProperty,
            ParameterDescription parameterDescription
    ) {
        return switch (parameterDescription.type()) {
            case FILE -> handleFileField(
                    serviceModuleReservation,
                    mapper.fromJson(initialDynamicProperty, InitFileUploadRequest.class)
            );
            case TEXT -> handleTextField(initialDynamicProperty, parameterDescription);
        };
    }

    private StorageAccessRequest handleFileField(
            ServiceModule serviceModuleReservation,
            InitFileUploadRequest initFileUploadRequest
    ) {
        return fileService.prepareExternalUpload(
                initFileUploadRequest,
                filePathProvider.serviceModuleFilePath(serviceModuleReservation),
                () -> {}
        );
    }

    private Object handleTextField(String initialDynamicProperty, ParameterDescription parameterDescription) {
        // doesn't require additional action for now (may require validation in the future)
        return null;
    }
}
