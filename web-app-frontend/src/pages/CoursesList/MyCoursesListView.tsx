import { Box, Button, Container, Grid, Stack, Typography } from '@mui/material';
import { getApis } from 'api/initialize-apis';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { PageHeaderText } from 'common/Styled/PageHeaderText';
import { stylesColumnCenteredHorizontal, stylesRowWithItemsAtTheEnd } from 'common/styles';
import { useErrorHandledQuery } from 'hooks/query/useErrorHandledQuery';
import { useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { useAppSelector } from 'services/store';
import { hasRole, TEACHER, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { CourseSummaryView } from './components/CourseSummaryView';

export const MyCoursesListView = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const { isApiError, errorHandler, consumeErrorAction, isApiErrorSet, pushApiError, removeApiError } =
        useApiErrorHandling({});
    const auth = useAppSelector((state) => state.auth.authData);

    const { data: courses, isLoading } = useErrorHandledQuery(
        'courses',
        () => getApis().courseApi.getUserCourses(),
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
                <>
                    <Grid item xs={6}>
                        <PageHeaderText text={t('myCourses.header')} />
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
                {isLoading || (courses && courses.length > 0) ? (
                    <Stack spacing={2} px={2}>
                        {courses?.map((course) => (
                            <CourseSummaryView key={course.id} courseSummary={course} />
                        ))}
                    </Stack>
                ) : (
                    <Box>
                        <Box sx={{ ...stylesColumnCenteredHorizontal, justifyContent: 'center', mt: 8 }}>
                            <Typography sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }} variant="h4">
                                {t(`myCourses.empty.${hasRole(auth, TEACHER) ? 'teacher' : 'student'}`)}
                            </Typography>
                            <WithRole roles={[TEACHER]}>
                                <Button
                                    variant="contained"
                                    sx={{ mt: 4, px: 4, py: 2 }}
                                    onClick={() => navigate(PageRoutes.CREATE_COURSE)}
                                >
                                    <Typography variant="h5">{t('myCourses.createCourseButton')}</Typography>
                                </Button>
                            </WithRole>
                        </Box>
                    </Box>
                )}
            </Container>
        </PageContainer>
    );
};
