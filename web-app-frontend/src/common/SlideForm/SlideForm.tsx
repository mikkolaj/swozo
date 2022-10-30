import { Box, Divider, Grid, Step, StepLabel, Stepper } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { Form, Formik, FormikProps, FormikValues } from 'formik';
import _ from 'lodash';
import { ComponentProps, RefObject } from 'react';
import { useTranslation } from 'react-i18next';
import { SlideProps } from './util';

type SlideConstuctor<T extends FormikValues> = (
    slideProps: SlideProps,
    formikProps: FormikProps<T>
) => JSX.Element;

type Props<T extends FormikValues> = ComponentProps<typeof Formik> & {
    initialValues: T;
    innerRef: RefObject<FormikProps<T>>;
    titleI18n: string;
    slidesI18n: string;
    buttons: JSX.Element;
    currentSlide: number;
    slideConstructors: SlideConstuctor<T>[];
    slidesWithErrors: number[];
};

export function SlideForm<T extends FormikValues>({
    initialValues,
    titleI18n,
    slidesI18n,
    buttons,
    innerRef,
    currentSlide,
    slideConstructors,
    slidesWithErrors,
    ...formikProps
}: Props<T>) {
    const { t } = useTranslation();
    const slideCount = slideConstructors.length;

    return (
        // no idea what overwrites this padding bottom, but couldnt find another way
        <PageContainer
            sx={{ paddingBottom: '0px !important' }}
            header={
                <>
                    <Grid item xs={12}>
                        <PageHeaderText text={t(titleI18n)} />
                    </Grid>
                    <Grid item xs={12}>
                        <Stepper sx={{ mt: 2 }} activeStep={currentSlide} alternativeLabel>
                            {_.range(slideCount).map((_, idx) => (
                                <Step key={idx}>
                                    <StepLabel error={slidesWithErrors.includes(idx)}>
                                        {t(`${slidesI18n}.${idx}.title`)}
                                    </StepLabel>
                                </Step>
                            ))}
                        </Stepper>
                    </Grid>
                </>
            }
        >
            <Box sx={{ px: 2, mb: 'auto', marginX: '5%' }}>
                <Formik initialValues={initialValues} innerRef={innerRef} {...formikProps}>
                    {(formikProps) => (
                        <Form>
                            {slideConstructors[currentSlide](
                                {
                                    nameBuilder: (name) => `${currentSlide}.${name}`,
                                },
                                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                                formikProps as any
                            )}
                        </Form>
                    )}
                </Formik>
            </Box>

            <Divider sx={{ mt: 4 }} />

            <Box sx={{ p: 1, justifyContent: 'flex-end' }}>{buttons}</Box>
        </PageContainer>
    );
}
