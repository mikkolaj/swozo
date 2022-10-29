import { Button, Container, Grid, Stack, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useDeleteServiceModule } from 'hooks/query/useDeleteServiceModule';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { TECHNICAL_TEACHER, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { ModuleSummaryView } from './components/MyModuleSummaryView';

export const MyModulesListView = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: modules } = useErrorHandledQuery(
        ['modules', 'summary', 'me'],
        () => getApis().serviceModuleApi.getUserModulesSummary(),
        pushApiError,
        removeApiError
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
                        <Typography variant="h4" component="div">
                            {t('myModules.header')}
                        </Typography>
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <WithRole roles={[TECHNICAL_TEACHER]}>
                            <Button onClick={() => navigate(PageRoutes.CREATE_MODULE)}>
                                {t('myModules.createModuleButton')}
                            </Button>
                        </WithRole>
                    </Grid>
                </>
            }
        >
            <Container>
                <Stack spacing={2} px={2}>
                    {modules?.map((module) => (
                        <ModuleSummaryView
                            key={module.id}
                            moduleSummary={module}
                            onDelete={() => serviceModuleDeleteMutation.mutate(`${module.id}`)}
                        />
                    ))}
                </Stack>
            </Container>
        </PageContainer>
    );
};
