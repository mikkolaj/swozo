import { Card, CardContent, Container } from '@mui/material';
import { ComponentProps, PropsWithChildren } from 'react';

export const PageContainer = ({
    children,
    ...props
}: PropsWithChildren<ComponentProps<typeof CardContent>>) => {
    return (
        <Container>
            <Card sx={{ position: 'relative', borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
                <CardContent {...props}>{children}</CardContent>
            </Card>
        </Container>
    );
};
