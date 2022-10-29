import { Box, Checkbox, Divider, FormControlLabel, MenuItem, Typography } from '@mui/material';
import { ServiceConfig } from 'api';
import { FormInputField } from 'common/Input/FormInputField';
import { FormSelectField } from 'common/Input/FormSelectField';
import { RichTextEditor } from 'common/Input/RichTextEditor';
import { SlideProps } from 'common/SlideForm/util';
import { stylesRowCenteredVertical } from 'common/styles';
import { FormikProps } from 'formik';
import _ from 'lodash';
import { ChangeEvent, MutableRefObject, RefObject, useEffect, useMemo } from 'react';
import { useTranslation } from 'react-i18next';
import { DynamicFormFields, DynamicFormValueRegistry, ModuleValues } from '../util/types';
import { DynamicModuleInfoForm } from './DynamicModuleInfoForm';

type Props = SlideProps & {
    values: ModuleValues;
    supportedServices: ServiceConfig[];
    dynamicFormRef: RefObject<FormikProps<DynamicFormFields>>;
    dynamicFormValueRegistryRef: MutableRefObject<DynamicFormValueRegistry>;
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    handleChange: (e: ChangeEvent<any>) => void;
    setFieldValue: (name: string, value: unknown) => void;
};

export const ModuleInfoForm = ({
    nameBuilder,
    values,
    supportedServices,
    handleChange,
    setFieldValue,
    dynamicFormRef,
    dynamicFormValueRegistryRef,
}: Props) => {
    const { t } = useTranslation();
    const serviceConfig = useMemo(() => {
        return supportedServices.find((cfg) => cfg.serviceName === values.service);
    }, [supportedServices, values.service]);

    useEffect(() => {
        dynamicFormValueRegistryRef.current = {};
    }, [serviceConfig, dynamicFormValueRegistryRef]);

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
                {supportedServices.map(({ serviceName }) => (
                    <MenuItem key={serviceName} value={serviceName}>
                        {_.capitalize(serviceName)}
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
                            width: '100%',
                        }}
                    >
                        <DynamicModuleInfoForm
                            dynamicFormRef={dynamicFormRef}
                            serviceConfig={serviceConfig}
                            currentValuesRef={dynamicFormValueRegistryRef}
                        />
                    </Box>
                    <Divider sx={{ width: '75%', mt: 2 }} />
                </>
            )}

            <Typography sx={{ mt: 2 }} variant="subtitle1">
                {t('createModule.slides.0.form.instructions.teacher')}
            </Typography>
            <RichTextEditor
                wrapperSx={{ width: '75%' }}
                name={nameBuilder('teacherInstruction')}
                value={values.teacherInstruction}
                setFieldValue={setFieldValue}
            />

            <Typography sx={{ mt: 2 }} variant="subtitle1">
                {t('createModule.slides.0.form.instructions.student')}
            </Typography>
            <RichTextEditor
                wrapperSx={{ width: '75%' }}
                name={nameBuilder('studentInstruction')}
                value={values.studentInstruction}
                setFieldValue={setFieldValue}
            />

            <FormControlLabel
                sx={{ mt: 2 }}
                control={<Checkbox checked={values.isPublic} value={values.isPublic} />}
                label={t('createModule.slides.0.form.public')}
                name={nameBuilder('isPublic')}
                onChange={handleChange}
            />
        </>
    );
};
