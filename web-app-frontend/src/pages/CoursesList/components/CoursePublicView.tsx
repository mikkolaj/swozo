import { Box, Button, Card, CardContent, Grid, Typography } from '@mui/material';
import { CourseSummaryDto } from 'api';
import { stylesColumnCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { formatDate } from 'utils/util';

type Props = {
    courseSummary: CourseSummaryDto;
};

export const CoursePublicView = ({ courseSummary }: Props) => {
    const { t } = useTranslation();

    return (
        <Card sx={{ boxShadow: 3 }}>
            <CardContent>
                <Grid container>
                    <Grid item xs={8}>
                        <Typography variant="h4" sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }}>
                            {courseSummary.name}
                        </Typography>
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
                            {t('publicCourses.createdAt')}
                        </Typography>
                        <Typography sx={{ mt: -1 }} variant="h6" component="div">
                            {formatDate(courseSummary.creationTime)}
                        </Typography>
                    </Grid>
                    <Grid item xs={8} sx={{ ...stylesColumnCenteredVertical, mt: 2 }}>
                        <Box sx={stylesColumnCenteredVertical}>
                            <Typography variant="body2">
                                {t('publicCourses.teacher', {
                                    firstName: courseSummary.teacher.name,
                                    lastName: courseSummary.teacher.surname,
                                })}
                            </Typography>
                        </Box>
                    </Grid>
                    <Grid item xs={4} sx={{ mt: 2 }}>
                        <Box sx={stylesRowWithItemsAtTheEnd}>
                            <Button variant="contained">
                                <Link
                                    target="_blank"
                                    rel="noopener"
                                    to={PageRoutes.JoinCourse(courseSummary.joinUUID)}
                                    style={{ textDecoration: 'none', color: 'inherit' }}
                                >
                                    {t('publicCourses.join')}
                                </Link>
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};
