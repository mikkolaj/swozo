import { Box, Container, Grid, Stack } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesRowCenteredVertical } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import _ from 'lodash';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';
import { getTranslated } from 'utils/util';
import { ServiceInfoView } from './ServiceInfoView';

export const ServicesListView = () => {
    const { i18n, t } = useTranslation();
    const [searchParams] = useSearchParams();
    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling({});
    const { data: supportedServices } = useErrorHandledQuery(
        'services',
        () => getApis().serviceModuleApi.getSupportedServices(),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );
    const [searchedServiceRef, setSearchedServiceRef] = useState<HTMLDivElement | null>();

    useEffect(() => {
        if (searchedServiceRef && searchedServiceRef) {
            searchedServiceRef.scrollIntoView({ behavior: 'smooth' });
        }
    }, [searchedServiceRef]);

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
                    {_.sortBy(supportedServices, (service) => service.displayName).map(
                        ({ serviceName, displayName, usageInstructionHtml }) => (
                            <Box key={serviceName} sx={{ position: 'relative' }}>
                                <div
                                    style={{ position: 'absolute', top: '-200px' }}
                                    ref={(el) => {
                                        if (displayName === searchParams.get('serviceName')) {
                                            setSearchedServiceRef(el);
                                        }
                                    }}
                                />
                                <ServiceInfoView
                                    key={serviceName}
                                    serviceName={displayName}
                                    usageInfo={getTranslated(i18n, usageInstructionHtml)}
                                />
                            </Box>
                        )
                    )}
                </Stack>
            </Container>
        </PageContainer>
    );
};
