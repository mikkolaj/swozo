import { Box, Checkbox, Divider, FormControlLabel, MenuItem, Typography } from '@mui/material';
import { ServiceConfigDto } from 'api';
import { FormInputField } from 'common/Input/FormInputField';
import { FormSelectField } from 'common/Input/FormSelectField';
import { SlideProps } from 'common/SlideForm/util';
import { stylesRowCenteredVertical } from 'common/styles';
import { FormikProps } from 'formik';
import { ChangeEvent, MutableRefObject, RefObject, useEffect, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { DynamicFormFields, ModuleValues } from '../util';
import { DynamicModuleInfoForm } from './dynamic/DynamicModuleInfoForm';

type Props = SlideProps & {
    values: ModuleValues;
    supportedServices: ServiceConfigDto[];
    dynamicFormRef: RefObject<FormikProps<DynamicFormFields>>;
    dynamicFormFieldsRef: MutableRefObject<DynamicFormFields>;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    handleChange: (e: ChangeEvent<any>) => void;
};

export const ModuleInfoForm = ({
    nameBuilder,
    values,
    supportedServices,
    handleChange,
    dynamicFormRef,
    dynamicFormFieldsRef,
}: Props) => {
    const { t } = useTranslation();
    const serviceConfig = useMemo(() => {
        return supportedServices.find((cfg) => cfg.serviceName === values.service);
    }, [supportedServices, values.service]);

    useEffect(() => {
        dynamicFormFieldsRef.current = {};
    }, [serviceConfig, dynamicFormFieldsRef]);

    return (
        <>
            <FormInputField
                name={nameBuilder('name')}
                textFieldProps={{ fullWidth: true }}
                wrapperSx={{ width: '50%' }}
                type="text"
                i18nLabel="createModule.slides.0.form.name"
            />
            <FormInputField
                name={nameBuilder('subject')}
                type="text"
                i18nLabel="createModule.slides.0.form.subject"
            />
            <FormInputField
                wrapperSx={{ width: '50%' }}
                name={nameBuilder('description')}
                type="text"
                textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                i18nLabel="createModule.slides.0.form.description"
            />
            <FormSelectField name={nameBuilder('service')} i18nLabel="createModule.slides.0.form.service">
                {supportedServices.map(({ serviceName, scheduleType }) => (
                    <MenuItem key={serviceName} value={scheduleType}>
                        {serviceName}
                    </MenuItem>
                ))}
            </FormSelectField>

            {serviceConfig && serviceConfig.parameterDescriptions && (
                <>
                    <Divider sx={{ width: '75%', mt: 2 }} />
                    <Typography sx={{ mt: 0 }} variant="subtitle1">
                        {t('createModule.slides.0.form.serviceConfig')}
                    </Typography>
                    <Box
                        sx={{
                            ...stylesRowCenteredVertical,
                            ml: 2,
                            mt: 2,
                        }}
                    >
                        <DynamicModuleInfoForm
                            dynamicFormRef={dynamicFormRef}
                            serviceConfig={serviceConfig}
                            currentValuesRef={dynamicFormFieldsRef}
                        />
                    </Box>
                    <Divider sx={{ width: '75%', mt: 2 }} />
                </>
            )}

            <Divider sx={{ width: '75%', mt: 2 }} />

            <FormInputField
                wrapperSx={{ width: '50%' }}
                name={nameBuilder('instructions')}
                type="text"
                textFieldProps={{ multiline: true, fullWidth: true, required: false }}
                i18nLabel="createModule.slides.0.form.instructions"
            />
            <FormControlLabel
                sx={{ mt: 2 }}
                control={<Checkbox value={values.isPublic} />}
                label={t('createModule.slides.0.form.public')}
                name={nameBuilder('isPublic')}
                onChange={handleChange}
            />
        </>
    );
};
