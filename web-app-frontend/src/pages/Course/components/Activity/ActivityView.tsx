import { Box, Card, CardContent, Typography } from '@mui/material';
import { Container } from '@mui/system';
import { useState } from 'react';
import { Activity } from 'utils/mocks';
import { ActivityActionButton } from './components/ActivityActionButton';
import { InstructionsModal } from './components/InstructionsModal';
import { LinksModal } from './components/LinksModal';

type Props = {
    activity: Activity;
};

export const ActivityView = ({ activity }: Props) => {
    const [linksModalOpen, setLinksModalOpen] = useState(false);
    const [instructionsModalOpen, setInstructionsModalOpen] = useState(false);

    return (
        <Box>
            <Card>
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
                            onClick={() => setInstructionsModalOpen(true)}
                            textPath="course.activity.instructions"
                        />
                    </Container>
                </CardContent>
            </Card>

            <LinksModal activity={activity} open={linksModalOpen} onClose={() => setLinksModalOpen(false)} />

            <InstructionsModal
                activity={activity}
                open={instructionsModalOpen}
                onClose={() => setInstructionsModalOpen(false)}
            />
        </Box>
    );
};
