package com.swozo.api.web.servicemodule.dynamic;

import com.swozo.api.web.servicemodule.dynamic.fields.DynamicFieldHandler;
import com.swozo.api.web.servicemodule.request.FinishServiceModuleCreationRequest;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.properties.FieldType;
import com.swozo.persistence.ServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DynamicPropertiesHelper {
    private final Map<FieldType, DynamicFieldHandler> fieldHandlers;

    @Autowired
    public DynamicPropertiesHelper(List<DynamicFieldHandler> fieldHandlers) {
        this.fieldHandlers = fieldHandlers.stream().collect(Collectors.toMap(
                DynamicFieldHandler::getType,
                Function.identity()
        ));
    }

    public Optional<Object> handleFieldForReservation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            ParameterDescription parameterDescription
    ) {
        return getHandler(parameterDescription.type())
                .handleForServiceModuleReservation(
                        fieldName,
                        serviceModuleReservation,
                        request,
                        parameterDescription
                );
    }

    public String handleFieldForCreation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            FinishServiceModuleCreationRequest request,
            ParameterDescription parameterDescription
    ) {
        return getHandler(parameterDescription.type())
                .handleForServiceModuleCreation(
                        fieldName,
                        serviceModuleReservation,
                        request,
                        parameterDescription
                );
    }

    @Async
    public void handleCleanup(Map<String, String> oldValues, Map<String, ParameterDescription> parameterDescriptionsByName) {
        oldValues.keySet().forEach(fieldName -> {
           var oldValue = oldValues.get(fieldName);
           var param = parameterDescriptionsByName.get(fieldName);
           getHandler(param.type()).cleanup(oldValue, param);
        });
    }

    public String decodeValue(String storedValue, ParameterDescription parameterDescription) {
        return getHandler(parameterDescription.type()).decodeValue(storedValue, parameterDescription);
    }

    private DynamicFieldHandler getHandler(FieldType fieldType) {
        return Optional.ofNullable(fieldHandlers.get(fieldType)).orElseThrow();
    }
}
