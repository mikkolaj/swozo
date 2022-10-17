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
     * @return empty optional if not further client-side actions are required for that field,
     *         otherwise object that should allow these actions to be executed
     */
    Optional<Object> handleForServiceModuleReservation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            ReserveServiceModuleRequest request,
            ParameterDescription parameterDescription
    );

    /**
     * @return final value that should be saved for this field
     */
    String handleForServiceModuleCreation(
            String fieldName,
            ServiceModule serviceModuleReservation,
            FinishServiceModuleCreationRequest request,
            ParameterDescription parameterDescription
    );
}
