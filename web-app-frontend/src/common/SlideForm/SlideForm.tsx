import { Box, Grid, Step, StepLabel, Stepper, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { Bar } from 'common/Styled/Bar';
import { FormikProps } from 'formik';
import { PropsWithChildren, Ref } from 'react';
import { useTranslation } from 'react-i18next';
import { range } from 'utils/utils';

type Props = {
    titlePath: string;
    slidesPath: string;
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
    titlePath,
    slidesPath,
    buttons,
    slideCount,
    currentSlide,
    children,
}: PropsWithChildren<Props>) => {
    const { t } = useTranslation();

    return (
        <PageContainer>
            <Grid container sx={{ mb: 1 }}>
                <Grid item xs={12}>
                    <Typography variant="h4" component="div">
                        {t(titlePath)}
                    </Typography>
                </Grid>
                <Grid item xs={12}>
                    <Stepper sx={{ mt: 2 }} activeStep={currentSlide} alternativeLabel>
                        {range(slideCount).map((_, idx) => (
                            <Step key={idx}>
                                <StepLabel>{t(`${slidesPath}.${idx}.title`)}</StepLabel>
                            </Step>
                        ))}
                    </Stepper>
                </Grid>
            </Grid>
            <Bar sx={{ mt: 2, mb: 5 }} />

            <Box sx={{ mt: 4, marginX: '5%' }}>{children}</Box>

            <Bar sx={{ mt: 2 }} />

            {buttons}
        </PageContainer>
    );
};
