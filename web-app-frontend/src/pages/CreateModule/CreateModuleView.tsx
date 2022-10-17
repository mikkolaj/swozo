import { Grid } from '@mui/material';
import { ReserveServiceModuleRequest } from 'api';
import { getApis } from 'api/initialize-apis';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { NextSlideButton } from 'common/SlideForm/buttons/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/buttons/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikProps } from 'formik';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import _ from 'lodash';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation } from 'react-query';
import { extractValueForReservation } from './components/dynamic/utils';
import { ModuleInfoForm } from './components/ModuleInfoForm';
import { ModuleSpecsForm } from './components/ModuleSpecsForm';
import { Summary } from './components/Summary';
import {
    DynamicFormFields,
    DynamicFormValueRegistry,
    FormValues,
    initialModuleValues,
    MODULE_INFO_SLIDE,
    MODULE_SPECS_SLIDE,
} from './util';

const initialValues: FormValues = {
    [MODULE_INFO_SLIDE]: initialModuleValues(),
    [MODULE_SPECS_SLIDE]: {
        environment: 'isolated',
        storage: 1,
        cpu: 'big',
        ram: 'big',
    },
};

export const CreateModuleView = () => {
    const { t } = useTranslation();
    const [currentSlide, setCurrentSlide] = useState(0);

    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: supportedServices } = useErrorHandledQuery(
        'services',
        () => getApis().serviceModuleApi.getSupportedServices(),
        pushApiError,
        removeApiError
    );

    const reserveServiceModuleMutation = useMutation(
        (reserveServiceModuleRequest: ReserveServiceModuleRequest) =>
            getApis().serviceModuleApi.reserveServiceModuleCreation({ reserveServiceModuleRequest }),
        {
            onSuccess: (resp) => {
                console.log(resp);
            },
        }
    );

    const formRef = useRef<FormikProps<FormValues>>(null);
    const dynamicFormRef = useRef<FormikProps<DynamicFormFields>>(null);
    const dynamicFormValueRegistryRef = useRef<DynamicFormValueRegistry>({});

    useEffect(() => {
        if (supportedServices && supportedServices.length > 0) {
            initialValues[MODULE_INFO_SLIDE].service = supportedServices[0].serviceName;
        }
    }, [supportedServices]);

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    if (!supportedServices) {
        return <PageContainerWithLoader />;
    }

    return (
        <SlideForm
            titleI18n="createModule.title"
            slidesI18n="createModule.slides"
            currentSlide={currentSlide}
            initialValues={initialValues}
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            innerRef={formRef as any}
            slidesWithErrors={[]}
            slideConstructors={[
                (slideProps, { values, handleChange, setFieldValue }) => (
                    <ModuleInfoForm
                        {...slideProps}
                        supportedServices={supportedServices}
                        values={values[MODULE_INFO_SLIDE]}
                        handleChange={handleChange}
                        setFieldValue={setFieldValue}
                        dynamicFormRef={dynamicFormRef}
                        dynamicFormValueRegistryRef={dynamicFormValueRegistryRef}
                    />
                ),
                (slideProps, _) => <ModuleSpecsForm {...slideProps} />,
                (_) => <Summary />,
            ]}
            onSubmit={() => {
                console.log('submit');
                console.log(dynamicFormValueRegistryRef.current);
                const values = formRef.current?.values;
                if (!values) return;
                const info = values[0];
                reserveServiceModuleMutation.mutate({
                    dynamicProperties: _.mapValues(
                        dynamicFormValueRegistryRef.current,
                        ({ type, associatedValue }) => extractValueForReservation(type, associatedValue)
                    ),
                    name: info.name,
                    instructionsFromTechnicalTeacher: info.instructions,
                    isPublic: info.isPublic,
                    scheduleTypeName: info.service,
                    subject: info.subject,
                });
            }}
            buttons={
                <Grid container>
                    <Grid item xs={6}>
                        <PreviousSlideButton
                            currentSlide={currentSlide}
                            label={t('createModule.buttons.back')}
                            onBack={setCurrentSlide}
                        />
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <NextSlideButton
                            currentSlide={currentSlide}
                            slideCount={3}
                            label={t('createModule.buttons.next')}
                            lastSlideLabel={t('createModule.finish')}
                            onNext={(slideNum) => setCurrentSlide(slideNum)}
                            onFinish={() => formRef.current?.handleSubmit()}
                        />
                    </Grid>
                </Grid>
            }
        />
    );
};
