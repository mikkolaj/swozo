import { Box, Container, Grid, Stack, Typography } from '@mui/material';
import { PageContainer } from 'common/PageContainer/PageContainer';
import { Bar } from 'common/Styled/Bar';
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
            <PageContainer>
                <Grid container>
                    <Grid item xs={6}>
                        <Typography variant="h4" component="div">
                            {course.name}
                        </Typography>
                    </Grid>
                </Grid>
                <Bar sx={{ mt: 1 }} />
                <Container sx={{ marginTop: 4 }}>
                    <Stack spacing={2}>
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
