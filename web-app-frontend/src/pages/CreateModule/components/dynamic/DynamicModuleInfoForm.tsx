import { Box } from '@mui/material';
import { ServiceConfig } from 'api';
import { Formik, FormikProps } from 'formik';
import _ from 'lodash';
import { DynamicFormFields, DynamicFormValueRegistry } from 'pages/CreateModule/util';
import { MutableRefObject, RefObject } from 'react';
import { DynamicField } from './DynamicField';
import { getInitialValue } from './utils';

type Props = {
    serviceConfig: ServiceConfig;
    currentValuesRef: MutableRefObject<DynamicFormValueRegistry>;
    dynamicFormRef: RefObject<FormikProps<DynamicFormFields>>;
};

export const DynamicModuleInfoForm = ({ currentValuesRef, serviceConfig, dynamicFormRef }: Props) => {
    const initialValues = !_.isEmpty(currentValuesRef.current)
        ? _.mapValues(currentValuesRef.current, ({ fieldValue }) => fieldValue)
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
                <Box sx={{ width: '100%' }}>
                    {serviceConfig.parameterDescriptions.map((param, idx) => (
                        <Box
                            key={idx}
                            sx={{
                                width: '100%',
                                mb: idx + 1 < serviceConfig.parameterDescriptions.length ? 2 : 0,
                            }}
                        >
                            <DynamicField
                                param={param}
                                setFieldValue={(val, type) => {
                                    setFieldValue(param.name, val);
                                    currentValuesRef.current[param.name] = {
                                        ...currentValuesRef.current[param.name],
                                        fieldValue: val,
                                        type,
                                    };
                                }}
                                setAssociatedValue={(val) => {
                                    currentValuesRef.current[param.name] = {
                                        ...currentValuesRef.current[param.name],
                                        associatedValue: val,
                                    };
                                }}
                            />
                        </Box>
                    ))}
                </Box>
            )}
        </Formik>
    );
};
