import { Box, Button, Card, CardContent, Grid, Typography } from '@mui/material';
import { CourseDetailsResp } from 'api';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { STUDENT, WithRole } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { formatDate } from 'utils/util';

type Props = {
    courseSummary: CourseDetailsResp;
};

export const CourseSummaryView = ({ courseSummary }: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <Card sx={{ boxShadow: 3 }}>
            <CardContent>
                <Grid container>
                    <Grid item xs={8}>
                        <LinkedTypography
                            variant="h4"
                            to={PageRoutes.Course(courseSummary.id)}
                            text={courseSummary.name}
                        />
                        <Typography
                            variant="body1"
                            sx={{
                                opacity: 0.8,
                                minWidth: '40%',
                                width: 'fit-content',
                                borderBottom: '1px solid rgba(0,0,0, 0.3)',
                            }}
                        >
                            #{_.capitalize(courseSummary.subject)}
                        </Typography>
                    </Grid>
                    <Grid item xs={4} sx={{ textAlign: 'right' }}>
                        <Typography variant="h6" component="div">
                            {t('myCourses.course.lastActivity')}
                        </Typography>
                        <Typography sx={{ mt: -1 }} variant="h6" component="div">
                            {formatDate(courseSummary.lastActivity)}
                        </Typography>
                    </Grid>
                    <Grid item xs={8} sx={{ mt: 2, display: 'flex', alignItems: 'center' }}>
                        <WithRole roles={[STUDENT]}>
                            <Box display="flex" alignItems="center">
                                <Typography variant="body2">
                                    {t('myCourses.course.teacher', {
                                        name: `${courseSummary.teacher.name} ${courseSummary.teacher.surname} `,
                                    })}
                                </Typography>
                            </Box>
                        </WithRole>
                    </Grid>
                    <Grid item xs={4} sx={{ mt: 2 }}>
                        <Box display="flex" justifyContent="flex-end">
                            <Button
                                variant="contained"
                                onClick={() => navigate(PageRoutes.Course(courseSummary.id))}
                            >
                                {t('myCourses.course.button')}
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};
