import { Box, Button, Container, Grid, Stack, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { mockModuleSummaryList } from 'utils/mocks';
import { TECHNICAL_TEACHER, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { ModuleSummaryView } from './components/ModuleSummaryView';

export const ModulesListView = () => {
    const { t } = useTranslation();
    const [moduleSummaryItems] = useState(mockModuleSummaryList);
    const navigate = useNavigate();

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
                    {moduleSummaryItems.map((module) => (
                        <ModuleSummaryView key={module.id} moduleSummary={module} />
                    ))}
                </Stack>
                <Box sx={{ height: 1000 }} />
            </Container>
        </PageContainer>
    );
};
