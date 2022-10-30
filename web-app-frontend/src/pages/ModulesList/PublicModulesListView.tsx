import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { Box, Container, Grid, Stack } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { ButtonWithIconAndText } from 'common/Styled/ButtonWithIconAndText';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { SearchBar } from 'common/Styled/SearchBar';
import { stylesRowCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { PublicModuleSummaryView } from './components/PublicModuleSummaryView';
import { Filters, ServiceModuleFilter } from './ServiceModuleFilter';
import { filterModules } from './utils';

export const PublicModulesListView = () => {
    const { t } = useTranslation();
    const [showFilters, setShowFilters] = useState(false);
    const [orderedModules, setOrderedModules] = useState<ServiceModuleSummaryDto[]>([]);
    const [searchContent, setSearchContent] = useState('');
    const [filters, setFilters] = useState<Filters>({ service: '' });
    // TODO add pagination

    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: modules } = useErrorHandledQuery(
        ['modules', 'summary'],
        () => getApis().serviceModuleApi.getAllPublicServiceModules(),
        pushApiError,
        removeApiError
    );

    const { data: supportedServices } = useErrorHandledQuery(
        'services',
        () => getApis().serviceModuleApi.getSupportedServices(),
        pushApiError,
        removeApiError
    );

    useEffect(() => {
        if (modules) {
            setOrderedModules(filterModules(modules, searchContent, filters));
        }
    }, [filters, modules, searchContent]);

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <PageContainer
            header={
                <>
                    <Grid item xs={6}>
                        <Box sx={{ ...stylesRowCenteredVertical }}>
                            <PageHeaderText text={t('publicModules.header')} />
                            <SearchBar
                                sx={{ ml: 3 }}
                                placeholder={t('publicModules.searchBoxPlaceholder')}
                                value={searchContent}
                                onChange={(e) => setSearchContent(e.target.value)}
                            />
                        </Box>
                    </Grid>
                    <Grid item xs={6} sx={{ ...stylesRowWithItemsAtTheEnd }}>
                        <ButtonWithIconAndText
                            Icon={showFilters ? ExpandLessIcon : ExpandMoreIcon}
                            textI18n="publicModules.filters"
                            onClick={() => setShowFilters((show) => !show)}
                        />
                    </Grid>
                    {showFilters && (
                        <Grid item xs={12}>
                            <ServiceModuleFilter
                                initialValues={filters}
                                supportedServices={supportedServices ?? []}
                                onModificationApplied={setFilters}
                            />
                        </Grid>
                    )}
                </>
            }
        >
            <Container>
                <Stack spacing={2} px={2}>
                    {orderedModules.map((module) => (
                        <PublicModuleSummaryView key={module.id} moduleSummary={module} />
                    ))}
                </Stack>
            </Container>
        </PageContainer>
    );
};
