import { Box, Card, CardContent, Typography } from '@mui/material';
import { Container } from '@mui/system';
import { CourseContext } from 'pages/Course/CourseView';
import { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Activity } from 'utils/mocks';
import { PageRoutes } from 'utils/routes';
import { ActivityActionButton } from './components/ActivityActionButton';
import { LinksModal } from './components/LinksModal';

type Props = {
    activity: Activity;
};

export const ActivityView = ({ activity }: Props) => {
    const navigate = useNavigate();
    const course = useContext(CourseContext);
    const [linksModalOpen, setLinksModalOpen] = useState(false);
    if (!course) {
        navigate(PageRoutes.HOME);
        return <></>;
    }

    return (
        <Box>
            <Card sx={{ boxShadow: 3 }}>
                <CardContent>
                    <Typography component="h1" variant="h5" gutterBottom>
                        {activity.name}
                    </Typography>
                    <Container>
                        <ActivityActionButton
                            onClick={() => setLinksModalOpen(true)}
                            textPath="course.activity.links"
                        />
                        <ActivityActionButton
                            onClick={() => navigate(PageRoutes.ActivityInstructions(course.id, activity.id))}
                            textPath="course.activity.instructions"
                        />
                    </Container>
                </CardContent>
            </Card>

            <LinksModal activity={activity} open={linksModalOpen} onClose={() => setLinksModalOpen(false)} />
        </Box>
    );
};
