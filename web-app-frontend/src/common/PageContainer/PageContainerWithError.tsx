import SentimentVeryDissatisfiedOutlinedIcon from '@mui/icons-material/SentimentVeryDissatisfiedOutlined';
import { Box, Button, Card, CardContent, Container, Grid, Typography } from '@mui/material';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { stylesRowCenteredHorizontal, stylesRowWithSpaceBetweenItems } from 'common/styles';
import useWindowDimensions from 'hooks/useWindowDimensions';
import { ComponentProps, RefObject, useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';

type Props = ComponentProps<typeof CardContent> & {
    errorMessage?: string;
    navButtonMessage?: string;
    navigateTo?: string;
    customErrorContent?: JSX.Element;
};

export const PageContainerWithError = ({
    errorMessage,
    navButtonMessage,
    navigateTo,
    customErrorContent,
    sx,
    ...props
}: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const containerRef: RefObject<HTMLDivElement> = useRef(null);
    const { height } = useWindowDimensions();
    const [containerY, setContainerY] = useState(0);
    useEffect(() => {
        const rect = containerRef.current?.getBoundingClientRect();
        if (rect) setContainerY(rect.y);
    }, [containerRef]);

    return (
        <>
            <Container ref={containerRef}>
                <Card
                    sx={{
                        position: 'relative',
                        borderTopLeftRadius: 0,
                        borderTopRightRadius: 0,
                    }}
                >
                    <CardContent
                        sx={{
                            p: 0,
                            width: '100%',
                            height: height - containerY,
                            ...sx,
                        }}
                        {...props}
                    ></CardContent>
                </Card>
            </Container>
            <AbsolutelyCentered>
                <Box sx={{ ...stylesRowCenteredHorizontal, mb: 5, mt: -5 }}>
                    <SentimentVeryDissatisfiedOutlinedIcon sx={{ width: 150, height: 150 }} />
                </Box>
                {customErrorContent ?? (
                    <Grid container sx={{ margin: 'auto', width: '80%' }}>
                        <Grid item xs={12}>
                            <Typography variant="h3">{errorMessage ?? t('error.defaultMessage')}</Typography>
                        </Grid>
                        <Grid
                            item
                            xs={12}
                            sx={{
                                ...stylesRowWithSpaceBetweenItems,
                                mt: 2,
                            }}
                        >
                            <Button onClick={() => window.location.reload()}>{t('error.refresh')}</Button>
                            <Button
                                onClick={() => navigate(navigateTo ?? PageRoutes.HOME, { replace: true })}
                            >
                                {navButtonMessage ?? t('error.defaultNavigationBtn')}
                            </Button>
                        </Grid>
                    </Grid>
                )}
            </AbsolutelyCentered>
        </>
    );
};
