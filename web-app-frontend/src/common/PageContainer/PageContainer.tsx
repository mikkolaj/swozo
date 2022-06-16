import { Card, CardContent, Container, Divider, Grid } from '@mui/material';
import { ComponentProps, PropsWithChildren } from 'react';

type Props = ComponentProps<typeof CardContent> & {
    header?: JSX.Element;
};

export const PageContainer = ({ children, header, sx, ...props }: PropsWithChildren<Props>) => {
    return (
        <Container>
            <Card sx={{ position: 'relative', borderTopLeftRadius: 0, borderTopRightRadius: 0 }}>
                <CardContent sx={{ p: 0, ...sx }} {...props}>
                    {header && (
                        <>
                            <Grid container sx={{ p: 2 }}>
                                {header}
                            </Grid>
                            <Divider sx={{ mb: 4 }} />
                        </>
                    )}
                    {children}
                </CardContent>
            </Card>
        </Container>
    );
};
