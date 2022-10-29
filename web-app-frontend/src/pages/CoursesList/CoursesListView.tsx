import { Button, Container, Grid, Stack, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { TEACHER, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { CourseSummaryView } from './components/CourseSummaryView';

export const CoursesListView = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { isApiError, errorHandler, consumeErrorAction, pushApiError, removeApiError } =
        useApiErrorHandling({});

    const { data: courseSummaryItems } = useErrorHandledQuery(
        'courses',
        () => getApis().courseApi.getUserCourses(),
        pushApiError,
        removeApiError
    );

    if (isApiError && errorHandler?.shouldTerminateRendering) {
        return consumeErrorAction() ?? <></>;
    }

    return (
        <PageContainer
            header={
                <>
                    <Grid item xs={6}>
                        <Typography variant="h4" component="div">
                            {t('myCourses.header')}
                        </Typography>
                    </Grid>
                    <Grid item xs={6} sx={stylesRowWithItemsAtTheEnd}>
                        <WithRole roles={[TEACHER]}>
                            <Button onClick={() => navigate(PageRoutes.CREATE_COURSE)}>
                                {t('myCourses.createCourseButton')}
                            </Button>
                        </WithRole>
                    </Grid>
                </>
            }
        >
            <Container>
                <Stack spacing={2} px={2}>
                    {courseSummaryItems?.map((course) => (
                        <CourseSummaryView key={course.id} courseSummary={course} />
                    ))}
                </Stack>
            </Container>
        </PageContainer>
    );
};
