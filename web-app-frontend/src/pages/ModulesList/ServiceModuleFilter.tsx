import { Box, MenuItem } from '@mui/material';
import { ServiceConfig } from 'api';
import { FormSelectField } from 'common/Input/FormSelectField';
import { Form, Formik, useFormikContext } from 'formik';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    supportedServices: ServiceConfig[];
    initialValues: Filters;
    onModificationApplied: (filters: Filters) => void;
};

export type Filters = {
    service: string;
};

export const ServiceModuleFilter = ({ initialValues, supportedServices, onModificationApplied }: Props) => {
    const { t } = useTranslation();
    const ChangeObserver = () => {
        const { values } = useFormikContext<Filters>();
        useEffect(() => {
            onModificationApplied(values);
        }, [values]);
        return null;
    };

    return (
        <Box>
            <Formik initialValues={initialValues} onSubmit={() => undefined}>
                {() => (
                    <Form>
                        <ChangeObserver />
                        <FormSelectField
                            textFieldProps={{ required: false }}
                            name={'service'}
                            i18nLabel="createModule.slides.0.form.service"
                        >
                            <MenuItem value={''}>{t('publicModules.any')}</MenuItem>
                            {supportedServices.map(({ serviceName, displayName }) => (
                                <MenuItem key={serviceName} value={serviceName}>
                                    {displayName}
                                </MenuItem>
                            ))}
                        </FormSelectField>
                    </Form>
                )}
            </Formik>
        </Box>
    );
};
