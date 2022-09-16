import { Grid } from '@mui/material';
import { NextSlideButton } from 'common/SlideForm/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikProps } from 'formik';
import { Ref, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { GeneralInfoForm } from './components/GeneralInfoForm';
import { ModuleSpecsForm } from './components/ModuleSpecsForm';
import { Summary } from './components/Summary';

const SLIDE_COUNT = 3;

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
            slideCount={SLIDE_COUNT}
            currentSlide={currentSlide}
            buttons={
                <Grid container>
                    <Grid item xs={6}>
                        <PreviousSlideButton
                            currentSlide={currentSlide}
                            label={t('createModule.buttons.back')}
                            goBack={(toSlide) => setCurrentSlide(toSlide)}
                        />
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <NextSlideButton
                            currentSlide={currentSlide}
                            slideCount={SLIDE_COUNT}
                            label={t('createModule.buttons.next')}
                            lastSlideLabel={t('createModule.finish')}
                            goNext={() => formRef.current?.handleSubmit()}
                            finish={() => console.log('TODO')}
                        />
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
