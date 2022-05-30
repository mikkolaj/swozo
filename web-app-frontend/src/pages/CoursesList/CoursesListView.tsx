import { Box, Container, Grid, Stack, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { Bar } from 'common/Styled/Bar';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { mockCourseSummaryList } from 'utils/mocks';
import { CourseSummaryView } from './components/CourseSummaryView';

export const CoursesListView = () => {
    const [courseSummaryItems] = useState(mockCourseSummaryList);
    const { t } = useTranslation();

    return (
        <PageContainer>
            <Grid container>
                <Grid item xs={6}>
                    <Typography variant="h4" component="div">
                        {t('courses.header')}
                    </Typography>
                </Grid>
            </Grid>
            <Bar />
            <Container sx={{ marginTop: 4 }}>
                <Stack spacing={2}>
                    {courseSummaryItems.map((course) => (
                        <CourseSummaryView key={course.id} courseSummary={course} />
                    ))}
                </Stack>
                <Box sx={{ height: 1000 }} />
            </Container>
        </PageContainer>
    );
};
