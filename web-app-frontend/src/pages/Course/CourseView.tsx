import { Box, Container, Divider, Grid, Stack, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Course, mockCourse } from 'utils/mocks';
import { ActivityView } from './components/Activity/ActivityView';

export const CourseContext = React.createContext<Course | null>(null);

export const CourseView = () => {
    const { courseId } = useParams();
    const [course] = useState(mockCourse);

    if (courseId === undefined || +courseId !== course.id) {
        return <div>No such course in mock data</div>;
    }

    return (
        <CourseContext.Provider value={course}>
            <PageContainer sx={{ p: 0 }}>
                <Grid container sx={{ p: 2 }}>
                    <Grid item xs={6}>
                        <Typography variant="h4" component="div">
                            {course.name}
                        </Typography>
                    </Grid>
                </Grid>
                <Divider />
                <Container sx={{ mt: 4 }}>
                    <Stack spacing={2} sx={{ px: 2 }}>
                        {course.activities.map((activity) => (
                            <ActivityView key={activity.id} activity={activity} />
                        ))}
                    </Stack>
                    <Box sx={{ height: 1000 }} />
                </Container>
            </PageContainer>
        </CourseContext.Provider>
    );
};
