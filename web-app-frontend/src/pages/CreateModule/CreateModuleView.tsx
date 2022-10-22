import { Grid } from '@mui/material';
import { ReserveServiceModuleRequest } from 'api';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { NextSlideButton } from 'common/SlideForm/buttons/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/buttons/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikProps } from 'formik';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAppDispatch } from 'services/store';
import { PageRoutes } from 'utils/routes';
import { ModuleInfoForm } from './components/ModuleInfoForm';
import { ModuleSpecsForm } from './components/ModuleSpecsForm';
import { Summary } from './components/Summary';
import {
    buildReserveServiceModuleRequest,
    createServiceModule,
    DynamicFormFields,
    DynamicFormValueRegistry,
    FormValues,
    initialModuleValues,
    mapToInitialValues,
    MODULE_INFO_SLIDE,
    MODULE_SPECS_SLIDE,
    preprocessSupportedServices,
    updateCacheAfterServiceModuleChange,
    updateServiceModule,
} from './util';

type Props = {
    editMode?: boolean;
};

const initialValues: FormValues = {
    [MODULE_INFO_SLIDE]: initialModuleValues(),
    [MODULE_SPECS_SLIDE]: {
        environment: 'isolated',
        storage: 1,
        cpu: 'big',
        ram: 'big',
    },
};

export const CreateModuleView = ({ editMode = false }: Props) => {
    const { moduleId } = useParams();
    const { t } = useTranslation();
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const queryClient = useQueryClient();
    const [currentSlide, setCurrentSlide] = useState(0);
    const formRef = useRef<FormikProps<FormValues>>(null);
    const dynamicFormRef = useRef<FormikProps<DynamicFormFields>>(null);
    const dynamicFormValueRegistryRef = useRef<DynamicFormValueRegistry>({});

    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: editModeInitialValues } = useErrorHandledQuery(
        ['modules', moduleId, 'edit'],
        () => getApis().serviceModuleApi.getFormDataForEdit({ serviceModuleId: +(moduleId ?? -1) }),
        pushApiError,
        removeApiError,
        editMode && moduleId !== undefined
    );

    const { data: supportedServices } = useErrorHandledQuery(
        'services',
        () => getApis().serviceModuleApi.getSupportedServices(),
        pushApiError,
        removeApiError
    );

    const createServiceModuleMutation = useMutation(
        async (reserveServiceModuleRequest: ReserveServiceModuleRequest) => {
            return editMode
                ? updateServiceModule(
                      reserveServiceModuleRequest,
                      moduleId ?? '',
                      dynamicFormValueRegistryRef.current,
                      dispatch
                  )
                : createServiceModule(
                      reserveServiceModuleRequest,
                      dynamicFormValueRegistryRef.current,
                      dispatch
                  );
        },
        {
            onSuccess: (resp) => {
                toast.success(t(`toast.${editMode ? 'serviceModuleUpdated' : 'serviceModuleCreated'}`));
                updateCacheAfterServiceModuleChange(resp, queryClient);
                navigate(PageRoutes.MY_MODULES);
            },
            onError: (err: ApiError) => {
                queryClient.invalidateQueries('modules');
                pushApiError(err);
            },
        }
    );

    useEffect(() => {
        if (supportedServices && supportedServices.length > 0) {
            initialValues[MODULE_INFO_SLIDE].service = supportedServices[0].serviceName;
        }
    }, [supportedServices]);

    if (editMode && moduleId === undefined) {
        navigate(PageRoutes.HOME);
        return <></>;
    }

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    if (editMode && (!editModeInitialValues || !supportedServices)) {
        return <PageContainerWithLoader />;
    }

    return (
        <SlideForm
            titleI18n="createModule.title"
            slidesI18n="createModule.slides"
            currentSlide={currentSlide}
            initialValues={
                editMode && editModeInitialValues ? mapToInitialValues(editModeInitialValues) : initialValues
            }
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            innerRef={formRef as any}
            slidesWithErrors={[]}
            slideConstructors={[
                (slideProps, { values, handleChange, setFieldValue }) => (
                    <ModuleInfoForm
                        {...slideProps}
                        supportedServices={preprocessSupportedServices(supportedServices, editMode, values)}
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
                if (!formRef.current?.values) return;

                createServiceModuleMutation.mutate(
                    buildReserveServiceModuleRequest(
                        formRef.current.values,
                        dynamicFormValueRegistryRef.current
                    )
                );
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
                            lastSlideLabel={t(
                                editMode ? 'createModule.finishEditMode' : 'createModule.finish'
                            )}
                            onNext={(slideNum) => setCurrentSlide(slideNum)}
                            onFinish={() => formRef.current?.handleSubmit()}
                        />
                    </Grid>
                </Grid>
            }
        />
    );
};
