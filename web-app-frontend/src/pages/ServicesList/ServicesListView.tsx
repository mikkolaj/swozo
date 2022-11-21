import { Box, Container, Grid, Stack } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesRowCenteredVertical } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useTranslation } from 'react-i18next';
import { getTranslated } from 'utils/util';
import { ServiceInfoView } from './ServiceInfoView';

export const ServicesListView = () => {
    const { i18n, t } = useTranslation();
    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling({});
    const { data: supportedServices } = useErrorHandledQuery(
        'services',
        () => getApis().serviceModuleApi.getSupportedServices(),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <PageContainer
            header={
                <Grid item xs={12}>
                    <Box sx={{ ...stylesRowCenteredVertical }}>
                        <PageHeaderText text={t('services.header')} />
                    </Box>
                </Grid>
            }
        >
            <Container>
                <Stack spacing={2} px={2}>
                    {supportedServices?.map(({ serviceName, displayName, usageInstructionHtml }) => (
                        <ServiceInfoView
                            key={serviceName}
                            serviceName={displayName}
                            usageInfo={getTranslated(i18n, usageInstructionHtml)}
                        />
                    ))}
                </Stack>
            </Container>
        </PageContainer>
    );
};
