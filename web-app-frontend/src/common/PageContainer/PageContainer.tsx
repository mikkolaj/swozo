import { Card, CardContent } from '@mui/material';
import { Container } from '@mui/system';

export const PageContainer: React.FC<React.PropsWithChildren<unknown>> = ({ children }) => {
    return (
        <Container>
            <Card sx={{ position: 'relative', borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
                <CardContent>{children}</CardContent>
            </Card>
        </Container>
    );
};
