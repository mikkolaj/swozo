import { Box } from '@mui/material';
import { ServiceConfigDto } from 'api';
import { Formik, FormikProps } from 'formik';
import _ from 'lodash';
import { DynamicFormFields } from 'pages/CreateModule/util';
import { MutableRefObject, RefObject } from 'react';
import { DynamicField } from './DynamicField';
import { getInitialValue } from './FieldFactory';

type Props = {
    serviceConfig: ServiceConfigDto;
    currentValuesRef: MutableRefObject<DynamicFormFields>;
    dynamicFormRef: RefObject<FormikProps<DynamicFormFields>>;
};

export const DynamicModuleInfoForm = ({ currentValuesRef, serviceConfig, dynamicFormRef }: Props) => {
    const initialValues = !_.isEmpty(currentValuesRef.current)
        ? currentValuesRef.current
        : Object.fromEntries(
              serviceConfig.parameterDescriptions.map(({ name, type }) => [name, getInitialValue(type)])
          );

    return (
        <Formik
            key={serviceConfig.serviceName}
            initialValues={initialValues}
            onSubmit={() => undefined}
            innerRef={dynamicFormRef}
        >
            {({ setFieldValue }) => (
                <Box>
                    {serviceConfig.parameterDescriptions.map((param, idx) => (
                        <Box
                            key={idx}
                            sx={{ mb: idx + 1 < serviceConfig.parameterDescriptions.length ? 2 : 0 }}
                        >
                            <DynamicField param={param} setValue={(val) => setFieldValue(param.name, val)} />
                        </Box>
                    ))}
                </Box>
            )}
        </Formik>
    );
};
