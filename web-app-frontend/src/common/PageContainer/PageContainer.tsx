import { Card, CardContent, Container, Divider, Grid } from '@mui/material';
import { useElementYPosition } from 'hooks/useElementYPosition';
import useWindowDimensions from 'hooks/useWindowDimensions';
import { ComponentProps, PropsWithChildren } from 'react';

type Props = ComponentProps<typeof CardContent> & {
    header?: JSX.Element;
};

export const PageContainer = ({ children, header, sx, ...props }: PropsWithChildren<Props>) => {
    const { height } = useWindowDimensions();
    const { ref: containerRef, yPos } = useElementYPosition<HTMLDivElement>();
    return (
        <Container ref={containerRef}>
            <Card
                sx={{
                    position: 'relative',
                    borderTopLeftRadius: 0,
                    borderTopRightRadius: 0,
                }}
            >
                <CardContent
                    sx={{ p: 0, minHeight: height - yPos, display: 'flex', flexDirection: 'column', ...sx }}
                    {...props}
                >
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
