import { Box, Button, Card, CardContent, Divider, Grid, Modal, Typography } from '@mui/material';
import { PropsWithChildren } from 'react';
import { AbsolutelyCentered } from './AbsolutetlyCentered';

type Props = {
    open: boolean;
    onClose: () => void;
    header: string;
    closeBtn?: string;
};

export const ScrollableCenteredModal = ({
    onClose,
    open,
    header,
    closeBtn,
    children,
}: PropsWithChildren<Props>) => {
    return (
        <Modal open={open} onClose={onClose} sx={{ width: '50%', margin: 'auto' }}>
            <AbsolutelyCentered>
                <Card
                    sx={{
                        borderRadius: 5,
                        border: 'none',
                        boxShadow: 3,
                    }}
                >
                    <CardContent
                        sx={{
                            minHeight: '500px',
                            maxHeight: '80vh',
                            p: 0,
                            overflowY: 'scroll',
                            '::-webkit-scrollbar': {
                                display: 'none',
                            },
                        }}
                    >
                        <Box
                            sx={{
                                position: 'sticky',
                                top: 0,
                                mt: 1,
                                mb: 2,
                                zIndex: 100000,
                                background: '#fff',
                            }}
                        >
                            <Typography sx={{ paddingX: 2 }} component="h1" variant="h6" gutterBottom>
                                {header}
                            </Typography>
                            <Divider />
                        </Box>
                        {children}
                    </CardContent>
                    {closeBtn && (
                        <Grid container sx={{ justifyContent: 'flex-end', p: 1 }}>
                            <Button onClick={onClose}>{closeBtn}</Button>
                        </Grid>
                    )}
                </Card>
            </AbsolutelyCentered>
        </Modal>
    );
};
