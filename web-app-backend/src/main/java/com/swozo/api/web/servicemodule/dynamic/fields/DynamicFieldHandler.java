package com.swozo.api.web.servicemodule.dynamic.fields;

import com.swozo.api.web.servicemodule.request.FinishServiceModuleCreationRequest;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.properties.FieldType;
import com.swozo.persistence.ServiceModule;

import java.util.Optional;

public interface DynamicFieldHandler {
    FieldType getType();

    /**
     * @return empty optional if no further client-side actions are required for that field,
     *         otherwise object that should allow these actions to be executed
     */
    default Optional<Object> handleForServiceModuleReservation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            ParameterDescription parameterDescription
    ) {
        return Optional.empty();
    }

    /**
     * @return final value that should be saved for this field
     */
    default String handleForServiceModuleCreation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            FinishServiceModuleCreationRequest request,
            ParameterDescription parameterDescription
    ) {
        return request.finalDynamicFieldValues().get(fieldName);
    }

    default String decodeValue(String storedValue, ParameterDescription parameterDescription) {
        return storedValue;
    }

    default void cleanup(String storedValue, ParameterDescription parameterDescription) {
        // nothing to do
    }
}
