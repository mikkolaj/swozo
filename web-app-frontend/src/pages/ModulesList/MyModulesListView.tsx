import { Box, Button, Container, Grid, Stack, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesColumnCenteredHorizontal, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useDeleteServiceModule } from 'hooks/query/useDeleteServiceModule';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { ModuleSummaryView } from './components/MyModuleSummaryView';

export const MyModulesListView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: modules, isLoading } = useErrorHandledQuery(
        ['modules', 'summary', 'me'],
        () => getApis().serviceModuleApi.getUserModulesSummary(),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    const { serviceModuleDeleteMutation } = useDeleteServiceModule(pushApiError);

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <PageContainer
            header={
                <>
                    <Grid item xs={6}>
                        <PageHeaderText text={t('myModules.header')} />
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <Button onClick={() => navigate(PageRoutes.CREATE_MODULE)}>
                            {t('myModules.createModuleButton')}
                        </Button>
                    </Grid>
                </>
            }
        >
            <Container>
                {isLoading || (modules && modules.length > 0) ? (
                    <Stack spacing={2} px={2}>
                        {modules?.map((module) => (
                            <ModuleSummaryView
                                key={module.id}
                                moduleSummary={module}
                                onDelete={() => serviceModuleDeleteMutation.mutate(`${module.id}`)}
                            />
                        ))}
                    </Stack>
                ) : (
                    <Box sx={{ ...stylesColumnCenteredHorizontal, justifyContent: 'center', mt: 8 }}>
                        <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }} variant="h4">
                            {t('myModules.empty')}
                        </Typography>
                        <Button
                            variant="contained"
                            sx={{ mt: 4, px: 4, py: 2 }}
                            onClick={() => navigate(PageRoutes.CREATE_MODULE)}
                        >
                            <Typography variant="h5">{t('myModules.createModuleButton')}</Typography>
                        </Button>
                    </Box>
                )}
            </Container>
        </PageContainer>
    );
};
