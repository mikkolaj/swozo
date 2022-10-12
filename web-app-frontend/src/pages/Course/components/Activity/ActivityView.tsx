import { Box, Card, CardContent, Typography } from '@mui/material';
import { Container } from '@mui/system';
import { ActivityDetailsDto } from 'api';
import { CourseContext } from 'pages/Course/CourseView';
import { useContext, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { formatDate, formatTime } from 'utils/util';
import { ActivityActionButton } from './components/ActivityActionButton';
import { LinksModal } from './components/LinksModal';

type Props = {
    activity: ActivityDetailsDto;
};

export const ActivityView = ({ activity }: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const course = useContext(CourseContext);
    const [linksModalOpen, setLinksModalOpen] = useState(false);
    if (!course) {
        navigate(PageRoutes.HOME, { replace: true });
        return <></>;
    }

    return (
        <Box>
            <Card sx={{ boxShadow: 3 }}>
                <CardContent sx={{ position: 'relative' }}>
                    <Box>
                        <Typography component="h1" variant="h5" gutterBottom>
                            {activity.name}
                        </Typography>
                    </Box>
                    <Box
                        sx={{
                            position: 'absolute',
                            right: 0,
                            top: 0,
                            pr: 2,
                            pt: 2,
                            pl: 1,
                            boxShadow: '0px 3px 8px rgb(100,100,100)',
                            borderBottomLeftRadius: 10,
                        }}
                    >
                        <Typography
                            sx={{ mb: -0.5, fontWeight: 500 }}
                            component="h1"
                            variant="h4"
                            gutterBottom
                        >
                            {formatDate(activity.startTime)}
                        </Typography>
                        <Typography component="h1" variant="h5" gutterBottom>
                            {t('course.activity.timeRange', {
                                startTime: formatTime(activity.startTime),
                                endTime: formatTime(activity.endTime),
                            })}
                        </Typography>
                    </Box>
                    <Container>
                        <ActivityActionButton
                            onClick={() => setLinksModalOpen(true)}
                            textI18n="course.activity.links"
                        />
                        <ActivityActionButton
                            onClick={() => navigate(PageRoutes.ActivityInstructions(course.id, activity.id))}
                            textI18n="course.activity.instructions"
                        />
                    </Container>
                </CardContent>
            </Card>

            <LinksModal activity={activity} open={linksModalOpen} onClose={() => setLinksModalOpen(false)} />
        </Box>
    );
};
