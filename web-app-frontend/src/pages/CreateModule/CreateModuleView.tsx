import { Grid } from '@mui/material';
import { NextSlideButton } from 'common/SlideForm/buttons/NextSlideButton';
import { PreviousSlideButton } from 'common/SlideForm/buttons/PreviousSlideButton';
import { SlideForm } from 'common/SlideForm/SlideForm';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { FormikProps } from 'formik';
import { Ref, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { ModuleInfoForm } from './components/ModuleInfoForm';
import { ModuleSpecsForm } from './components/ModuleSpecsForm';
import { Summary } from './components/Summary';
import { FormValues, initialModuleValues, MODULE_INFO_SLIDE, MODULE_SPECS_SLIDE } from './util';

export const CreateModuleView = () => {
    const { t } = useTranslation();
    const [currentSlide, setCurrentSlide] = useState(0);

    const initialValues: FormValues = {
        [MODULE_INFO_SLIDE]: initialModuleValues(),
        [MODULE_SPECS_SLIDE]: {
            environment: 'isolated',
            storage: 1,
            cpu: 'big',
            ram: 'big',
        },
    };

    const formRef: Ref<FormikProps<FormValues>> = useRef(null);

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
                (slideProps, { values, setValues, handleChange }) => (
                    <ModuleInfoForm
                        {...slideProps}
                        values={values[MODULE_INFO_SLIDE]}
                        setValues={(moduleInfoValues) => {
                            setValues({ ...values, [MODULE_INFO_SLIDE]: moduleInfoValues });
                        }}
                        handleChange={handleChange}
                    />
                ),
                (slideProps, _) => <ModuleSpecsForm {...slideProps} />,
                (_) => <Summary />,
            ]}
            onSubmit={(values) => {
                console.log('submit');
                console.log(values);
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
                            onNext={setCurrentSlide}
                            onFinish={() => console.log('TODO')}
                        />
                    </Grid>
                </Grid>
            }
        />
    );
};
