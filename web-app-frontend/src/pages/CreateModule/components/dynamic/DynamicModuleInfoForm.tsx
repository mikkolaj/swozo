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
                <div>
                    {serviceConfig.parameterDescriptions.map((param, idx) => (
                        <DynamicField
                            key={idx}
                            param={{ ...param, name: param.name }}
                            setValue={(val) => setFieldValue(param.name, val)}
                        />
                    ))}
                </div>
            )}
        </Formik>
    );
};
