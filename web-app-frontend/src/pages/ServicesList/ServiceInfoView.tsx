import { Box, Card, CardContent, Divider, Typography } from '@mui/material';
import './serviceInfo.css';

type Props = {
    serviceName: string;
    usageInfo: string;
};

export const ServiceInfoView = ({ serviceName, usageInfo }: Props) => {
    return (
        <Card sx={{ boxShadow: 3 }}>
            <CardContent>
                <Box sx={{}}>
                    <Typography variant="h4">{serviceName}</Typography>
                    <Divider sx={{ mt: 1, mb: 2 }} />
                    <span dangerouslySetInnerHTML={{ __html: usageInfo }} />
                </Box>
            </CardContent>
        </Card>
    );
};
