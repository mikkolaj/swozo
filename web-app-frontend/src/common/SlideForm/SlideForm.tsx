import { Box, Divider, Grid, Step, StepLabel, Stepper, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { FormikProps } from 'formik';
import _ from 'lodash';
import { PropsWithChildren, Ref } from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    titleI18n: string;
    slidesI18n: string;
    buttons: JSX.Element;
    slideCount: number;
    currentSlide: number;
};

export type SlideProps<T> = {
    formRef: Ref<FormikProps<T>>;
    initialValues: T;
    setValues: (values: T) => void;
};

export const SlideForm = ({
    titleI18n,
    slidesI18n,
    buttons,
    slideCount,
    currentSlide,
    children,
}: PropsWithChildren<Props>) => {
    const { t } = useTranslation();

    return (
        // no idea what overwrites this padding bottom, but couldnt find another way
        <PageContainer
            sx={{ paddingBottom: '0px !important' }}
            header={
                <>
                    <Grid item xs={12}>
                        <Typography variant="h4" component="div">
                            {t(titleI18n)}
                        </Typography>
                    </Grid>
                    <Grid item xs={12}>
                        <Stepper sx={{ mt: 2 }} activeStep={currentSlide} alternativeLabel>
                            {_.range(slideCount).map((_, idx) => (
                                <Step key={idx}>
                                    <StepLabel>{t(`${slidesI18n}.${idx}.title`)}</StepLabel>
                                </Step>
                            ))}
                        </Stepper>
                    </Grid>
                </>
            }
        >
            <Box sx={{ px: 2, mb: 'auto', marginX: '5%' }}>{children}</Box>

            <Divider sx={{ mt: 4 }} />

            <Box sx={{ p: 1, justifyContent: 'flex-end' }}>{buttons}</Box>
        </PageContainer>
    );
};
