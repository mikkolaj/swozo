import SentimentVeryDissatisfiedOutlinedIcon from '@mui/icons-material/SentimentVeryDissatisfiedOutlined';
import { Box, Button, Grid, Typography } from '@mui/material';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { stylesRowCenteredHorizontal, stylesRowWithSpaceBetweenItems } from 'common/styles';
import { ComponentProps } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { PageContainer } from './PageContainer';

type Props = ComponentProps<typeof PageContainer> & {
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
    ...props
}: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <PageContainer {...props}>
            <AbsolutelyCentered>
                <Box sx={{ ...stylesRowCenteredHorizontal, justifyContent: 'center', mb: 5, mt: -5 }}>
                    <SentimentVeryDissatisfiedOutlinedIcon sx={{ width: 150, height: 150 }} />
                </Box>
                {customErrorContent ?? (
                    <Grid container sx={{ margin: 'auto', maxWidth: '80%' }}>
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
        </PageContainer>
    );
};
