import { Button, Grid } from '@mui/material';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikProps } from 'formik';
import { Ref, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { GeneralInfoForm } from './components/GeneralInfoForm';
import { ModuleSpecsForm } from './components/ModuleSpecsForm';
import { Summary } from './components/Summary';

export const CreateModuleView = () => {
    const { t } = useTranslation();
    const [currentSlide, setCurrentSlide] = useState(0);
    const [moduleValues, setModuleValues] = useState({
        name: '',
        subject: '',
        description: '',
        service: 'Jupyter',
        serviceFile: '',
        instructions: '',
        isPublic: true,
    });

    const [moduleSpec, setModuleSpecs] = useState({
        environment: 'isolated',
        storage: 1,
        cpu: 'big',
        ram: 'big',
    });

    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    const formRef: Ref<FormikProps<any>> = useRef(null);

    return (
        <SlideForm
            titleI18n="createModule.title"
            slidesI18n="createModule.slides"
            slideCount={3}
            currentSlide={currentSlide}
            buttons={
                <Grid container>
                    <Grid item xs={6}>
                        {currentSlide > 0 && (
                            <Button
                                onClick={() => {
                                    setCurrentSlide(currentSlide - 1);
                                }}
                            >
                                {t('createModule.buttons.back')}
                            </Button>
                        )}
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <Button
                            sx={{ alignSelf: 'flex-end' }}
                            onClick={() => {
                                formRef.current?.handleSubmit();
                            }}
                        >
                            {t(currentSlide === 2 ? 'createModule.finish' : 'createModule.buttons.next')}
                        </Button>
                    </Grid>
                </Grid>
            }
        >
            {currentSlide === 0 && (
                <GeneralInfoForm
                    formRef={formRef}
                    initialValues={moduleValues}
                    setValues={(values) => {
                        setModuleValues(values);
                        setCurrentSlide(currentSlide + 1);
                    }}
                />
            )}
            {currentSlide === 1 && (
                <ModuleSpecsForm
                    formRef={formRef}
                    initialValues={moduleSpec}
                    setValues={(values) => {
                        setModuleSpecs(values);
                        setCurrentSlide(currentSlide + 1);
                    }}
                />
            )}
            {currentSlide === 2 && <Summary />}
        </SlideForm>
    );
};
