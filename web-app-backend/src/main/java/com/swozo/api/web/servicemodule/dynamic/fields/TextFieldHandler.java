package com.swozo.api.web.servicemodule.dynamic.fields;

import com.swozo.model.scheduling.properties.FieldType;
import org.springframework.stereotype.Component;

@Component
public class TextFieldHandler implements DynamicFieldHandler {
    @Override
    public FieldType getType() {
        return FieldType.TEXT;
    }
}
