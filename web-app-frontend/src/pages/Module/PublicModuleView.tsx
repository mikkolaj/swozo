import SportsEsportsIcon from '@mui/icons-material/SportsEsports';
import { Box, Grid, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageContainerWithLoader } from 'common/PageContainer/PageContainerWIthLoader';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesColumnCenteredVertical, stylesRow, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useRequiredParams } from 'hooks/useRequiredParams';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { formatDate, formatName } from 'utils/util';
import { SandboxModal } from './components/SandboxModal';
import { ServiceModuleGeneralInfo, StyledReadonlyField } from './components/ServiceModuleGeneralInfo';

export const PublicModuleView = () => {
    const [moduleId] = useRequiredParams(['moduleId']);
    const [sandboxModalOpen, setSandboxModalOpen] = useState(false);
    const { t } = useTranslation();

    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: serviceModule } = useErrorHandledQuery(
        ['modules', moduleId, 'summary'],
        () => getApis().serviceModuleApi.getServiceModuleSummary({ serviceModuleId: +moduleId }),
        pushApiError,
        removeApiError,
        isApiErrorSet
    );

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    if (!serviceModule) {
        return <PageContainerWithLoader />;
    }

    return (
        <PageContainer
            sx={{ p: 0 }}
            header={
                <>
                    <Grid item xs={6}>
                        <PageHeaderText text={t('myModule.header', { name: serviceModule.name })} />
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <ButtonWithIconAndText
                            textI18n="myModule.sandbox"
                            Icon={SportsEsportsIcon}
                            onClick={() => setSandboxModalOpen(true)}
                        />
                    </Grid>
                </>
            }
        >
            <Grid container sx={{ p: 2, pt: 0 }}>
                <Grid item xs={12}>
                    <Typography variant="h5" gutterBottom>
                        {t('myModule.generalInfo')}
                    </Typography>
                    <Box sx={{ ...stylesColumnCenteredVertical }}>
                        <Box sx={{ ...stylesRow }}>
                            <StyledReadonlyField
                                value={formatName(serviceModule.creator.name, serviceModule.creator.surname)}
                                i18nLabel="publicModule.authorName"
                            />
                            <StyledReadonlyField
                                value={serviceModule.creator.email}
                                i18nLabel="publicModule.authorEmail"
                            />
                        </Box>
                        <Box sx={{ ...stylesRow }}>
                            <StyledReadonlyField
                                value={formatDate(serviceModule.createdAt)}
                                i18nLabel="publicModule.createdAt"
                            />
                            <StyledReadonlyField
                                value={`${serviceModule.usedInActivitiesCount}`}
                                i18nLabel="publicModule.usedBy"
                            />
                        </Box>
                        <ServiceModuleGeneralInfo serviceModule={serviceModule} />
                    </Box>
                </Grid>
            </Grid>
            <SandboxModal
                open={sandboxModalOpen}
                onClose={() => setSandboxModalOpen(false)}
                serviceModule={serviceModule}
            />
        </PageContainer>
    );
};
