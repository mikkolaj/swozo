package com.swozo.api.web.servicemodule.dynamic.fields;

import com.swozo.api.web.servicemodule.request.FinishServiceModuleCreationRequest;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.properties.FieldType;
import com.swozo.persistence.ServiceModule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TextFieldHandler implements DynamicFieldHandler {
    @Override
    public FieldType getType() {
        return FieldType.TEXT;
    }

    @Override
    public Optional<Object> handleForServiceModuleReservation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            ParameterDescription parameterDescription
    ) {
        // no actions required yet
        return Optional.empty();
    }

    @Override
    public String handleForServiceModuleCreation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            FinishServiceModuleCreationRequest request,
            ParameterDescription parameterDescription
    ) {
        // maybe validate
        return request.finalDynamicFieldValues().get(fieldName);
    }
}
