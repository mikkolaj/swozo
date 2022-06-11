import { Card, CardContent } from '@mui/material';
import { Container } from '@mui/system';
import { PropsWithChildren } from 'react';

export const PageContainer = ({ children }: PropsWithChildren<unknown>) => {
    return (
        <Container>
            <Card sx={{ position: 'relative', borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
                <CardContent>{children}</CardContent>
            </Card>
        </Container>
    );
};
