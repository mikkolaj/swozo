import { Box, Button, Grid, Typography } from '@mui/material';
import { blue } from '@mui/material/colors';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { Bar } from 'common/Styled/Bar';
import { FC, Ref, useEffect, useRef } from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    titlePath: string;
    slidesPath: string;
    slideCount: number;
    currentSlide: number;
    setSlide: (newSlide: number) => void;
};

export const SlideForm: FC<React.PropsWithChildren<Props>> = ({
    titlePath,
    slidesPath,
    slideCount,
    currentSlide,
    setSlide,
    children,
}) => {
    const { t } = useTranslation();

    const barRef: Ref<HTMLDivElement> = useRef(null);
    const boxRef: Ref<HTMLDivElement> = useRef(null);
    const firstSlideRef: Ref<HTMLElement> = useRef(null);
    const lastSlideRef: Ref<HTMLElement> = useRef(null);

    useEffect(() => {
        if (!barRef.current || !firstSlideRef.current || !lastSlideRef.current || !boxRef.current) return;

        const r0 = boxRef.current.getBoundingClientRect();
        const r1 = firstSlideRef.current.getBoundingClientRect();
        const r2 = lastSlideRef.current.getBoundingClientRect();

        const style = barRef.current.style;

        style.width = `${r2.x - r1.x - 8}px`;
        style.top = `${r1.y - r0.y + r1.height / 2}px`;
        style.left = `${r1.x - r0.x + r1.width / 2}px`;
    }, [barRef, firstSlideRef, lastSlideRef, boxRef]);

    return (
        <PageContainer>
            <Grid container sx={{ mb: 1 }}>
                <Grid item xs={12}>
                    <Typography variant="h4" component="div">
                        {t(titlePath)}
                    </Typography>
                </Grid>
                <Grid item xs={12}>
                    <Box
                        ref={boxRef}
                        sx={{
                            position: 'relative',
                            display: 'flex',
                            flexDirection: 'row',
                            justifyContent: 'space-between',
                            mt: 2,
                            marginX: '15%',
                        }}
                    >
                        {Array(slideCount)
                            .fill(0)
                            .map((_, idx) => (
                                <Box key={idx}>
                                    <Box>{t(`${slidesPath}.${idx}.title`)}</Box>
                                    <Box
                                        ref={
                                            idx === 0
                                                ? firstSlideRef
                                                : idx === slideCount - 1
                                                ? lastSlideRef
                                                : undefined
                                        }
                                        sx={{
                                            display: 'flex',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                        }}
                                    >
                                        <Box
                                            sx={{
                                                width: '16px',
                                                height: '16px',
                                                border: `1px solid ${
                                                    currentSlide === idx ? blue['500'] : 'black'
                                                }`,
                                                position: 'relative',
                                                borderRadius: 100,
                                                zIndex: 100,
                                                background: currentSlide === idx ? blue['500'] : 'white',
                                            }}
                                        />
                                    </Box>
                                </Box>
                            ))}
                        <div
                            ref={barRef}
                            style={{
                                position: 'absolute',
                                borderBottom: '1px solid rgba(0, 0, 0, 0.4)',
                                zIndex: 99,
                            }}
                        ></div>
                    </Box>
                </Grid>
            </Grid>
            <Bar />

            <Box sx={{ mt: 4, marginX: '5%' }}>{children}</Box>

            <Bar />

            <Grid container sx={{ mt: 4 }}>
                <Grid item xs={6}>
                    {currentSlide > 0 && (
                        <Button onClick={() => setSlide(currentSlide - 1)}>{t('form.backButton')}</Button>
                    )}
                </Grid>
                <Grid
                    item
                    xs={6}
                    sx={{
                        display: 'flex',
                        flexDirection: 'row',
                        justifyContent: 'flex-end',
                    }}
                >
                    {currentSlide < slideCount - 1 && (
                        <Button sx={{ alignSelf: 'flex-end' }} onClick={() => setSlide(currentSlide + 1)}>
                            {t('form.nextButton')}
                        </Button>
                    )}
                </Grid>
            </Grid>
        </PageContainer>
    );
};
