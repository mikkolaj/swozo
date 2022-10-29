import { Card, CardContent, Modal, SxProps, Theme } from '@mui/material';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { PropsWithChildren } from 'react';

type Props = {
    open?: boolean;
    onClose: () => void;
    modalSx?: SxProps<Theme>;
    cardSx?: SxProps<Theme>;
};

export const CenteredModal = ({ open, onClose, modalSx, cardSx, children }: PropsWithChildren<Props>) => {
    return (
        <Modal open={open ?? true} onClose={onClose} sx={{ width: '20%', margin: 'auto', ...modalSx }}>
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
                            minHeight: '200px',
                            maxHeight: '50vh',
                            p: 0,
                            overflowY: 'scroll',
                            '::-webkit-scrollbar': {
                                display: 'none',
                            },
                            ...cardSx,
                        }}
                    >
                        {children}
                    </CardContent>
                </Card>
            </AbsolutelyCentered>
        </Modal>
    );
};
