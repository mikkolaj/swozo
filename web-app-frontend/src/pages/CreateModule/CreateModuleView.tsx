import { Grid } from '@mui/material';
import { ReserveServiceModuleRequest } from 'api';
import { ApiError, ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { NextSlideButton } from 'common/SlideForm/buttons/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/buttons/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { getSortedSlidesWithErrors } from 'common/SlideForm/util';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikErrors, FormikProps } from 'formik';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import { closeModal, ModalId, openModal } from 'services/features/modal/modalSlice';
import { useAppDispatch } from 'services/store';
import { PageRoutes } from 'utils/routes';
import { ModuleInfoForm } from './components/ModuleInfoForm';
import { ModuleSpecsForm } from './components/ModuleSpecsForm';
import { Summary } from './components/Summary';
import { createServiceModule, updateCacheAfterServiceModuleChange, updateServiceModule } from './util/api';
import {
    buildReserveServiceModuleRequest,
    flattenDynamicErrors,
    formatErrors,
    initialMdaValues,
    initialModuleValues,
    mapToInitialValues,
    preprocessSupportedServices,
} from './util/mapping';
import {
    DynamicFormFields,
    DynamicFormValueRegistry,
    FormValues,
    MODULE_INFO_SLIDE,
    MODULE_SPECS_SLIDE,
} from './util/types';

type Props = {
    editMode?: boolean;
};

const initialValues: FormValues = {
    [MODULE_INFO_SLIDE]: initialModuleValues(),
    [MODULE_SPECS_SLIDE]: initialMdaValues(true),
};

export const CreateModuleView = ({ editMode = false }: Props) => {
    const { moduleId } = useParams();
    const { t } = useTranslation();
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const queryClient = useQueryClient();
    const [currentSlide, setCurrentSlide] = useState(0);
    const [formattedApiErrors, setFormattedApiErrors] = useState<FormikErrors<FormValues>>();
    const formRef = useRef<FormikProps<FormValues>>(null);
    const dynamicFormRef = useRef<FormikProps<DynamicFormFields>>(null);
    const dynamicFormValueRegistryRef = useRef<DynamicFormValueRegistry>({});

    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: editModeInitialValues } = useErrorHandledQuery(
        ['modules', moduleId, 'edit'],
        () => getApis().serviceModuleApi.getFormDataForEdit({ serviceModuleId: +(moduleId ?? -1) }),
        pushApiError,
        removeApiError,
        isApiErrorSet,
        editMode && moduleId !== undefined
    );

    const { data: supportedServices } = useErrorHandledQuery(
        'services',
        () => getApis().serviceModuleApi.getSupportedServices(),
        pushApiError,
        removeApiError,
        isApiErrorSet
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
                updateCacheAfterServiceModuleChange(resp, queryClient, editMode);
                dispatch(closeModal(ModalId.MODULE_CREATION_IN_PROGRESS));
                navigate(PageRoutes.MY_MODULES);
            },
            onError: (err: ApiError) => {
                if (editMode) {
                    queryClient.invalidateQueries(['modules', moduleId]);
                }
                dispatch(closeModal(ModalId.MODULE_CREATION_IN_PROGRESS));

                if (err.errorType === ErrorType.VALIDATION_FAILED) {
                    const formattedErrors = formatErrors(t, err);
                    setFormattedApiErrors(formattedErrors);
                    setCurrentSlide(getSortedSlidesWithErrors(formattedErrors)[0]);
                } else {
                    pushApiError(err);
                }
            },
        }
    );

    useEffect(() => {
        if (supportedServices && supportedServices.length > 0) {
            initialValues[MODULE_INFO_SLIDE].service = supportedServices[0].serviceName;
        }
    }, [supportedServices]);

    useEffect(() => {
        if (formattedApiErrors) {
            formRef.current?.setErrors(formattedApiErrors);
        }
    }, [formattedApiErrors]);

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
            slidesWithErrors={getSortedSlidesWithErrors(formattedApiErrors ?? {})}
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
                        dynamicFormErrors={formattedApiErrors && flattenDynamicErrors(formattedApiErrors)}
                    />
                ),
                (slideProps, { values }) => (
                    <ModuleSpecsForm
                        serviceConfig={preprocessSupportedServices(supportedServices, editMode, values).find(
                            (service) => service.serviceName === values[MODULE_INFO_SLIDE].service
                        )}
                        values={values[MODULE_SPECS_SLIDE]}
                        editMode={editMode}
                        {...slideProps}
                    />
                ),
                (_, { values }) => <Summary editMode={editMode} formValues={values} />,
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
                            disabled={createServiceModuleMutation.isLoading}
                            currentSlide={currentSlide}
                            slideCount={3}
                            label={t('createModule.buttons.next')}
                            lastSlideLabel={t(
                                editMode ? 'createModule.finishEditMode' : 'createModule.finish'
                            )}
                            onNext={(slideNum) => setCurrentSlide(slideNum)}
                            onFinish={() => {
                                dispatch(
                                    openModal({
                                        modalProps: {
                                            id: ModalId.MODULE_CREATION_IN_PROGRESS,
                                            allowClose: false,
                                            textLines: [
                                                t(`createModule.modal.${editMode ? 'updating' : 'creating'}`),
                                            ],
                                        },
                                    })
                                );
                                formRef.current?.handleSubmit();
                            }}
                        />
                    </Grid>
                </Grid>
            }
        />
    );
};
