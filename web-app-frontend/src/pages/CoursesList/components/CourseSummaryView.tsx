import { Box, Button, Card, CardContent, Grid, Typography } from '@mui/material';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { CourseSummary } from 'utils/mocks';
import { PageRoutes } from 'utils/routes';

type Props = {
    courseSummary: CourseSummary;
};

export const CourseSummaryView: React.FC<Props> = ({ courseSummary }: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <Card sx={{ boxShadow: 3 }}>
            <CardContent>
                <Grid container>
                    <Grid item xs={8}>
                        <LinkedTypography
                            variant="h4"
                            to={PageRoutes.buildCourseRoute(courseSummary.id)}
                            text={courseSummary.name}
                        />
                    </Grid>
                    <Grid item xs={4} sx={{ textAlign: 'right' }}>
                        <Typography variant="h6" component="div">
                            {t('courses.course.lastActivity')}
                        </Typography>
                        <Typography sx={{ mt: -1 }} variant="h6" component="div">
                            {courseSummary.lastActivity}
                        </Typography>
                    </Grid>
                    <Grid item xs={8} sx={{ mt: 2, mb: -2 }}>
                        <Box display="flex" alignItems="center">
                            <Typography variant="body2">
                                {t('courses.course.teacher', { name: courseSummary.teacherName })}
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={4} sx={{ mt: 2, mb: -2 }} justifyContent="flex-end">
                        <Box display="flex" justifyContent="flex-end">
                            <Button onClick={() => navigate(PageRoutes.buildCourseRoute(courseSummary.id))}>
                                {t('courses.course.button')}
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};
