import {
    Box,
    Button,
    Card,
    CardContent,
    Divider,
    Grid,
    Modal,
    Paper,
    Tab,
    Tabs,
    Typography,
} from '@mui/material';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Activity } from 'utils/mocks';

type Props = {
    activity: Activity;
    open: boolean;
    onClose: () => void;
};

export const InstructionsModal = ({ activity, open, onClose }: Props) => {
    const { t } = useTranslation();
    const [currentTab, setCurrentTab] = useState(0);

    return (
        <Modal open={open} onClose={onClose}>
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
                            minWidth: '1000px',
                            minHeight: '500px',
                            maxHeight: '80vh',
                            p: 0,
                            overflowY: 'scroll',
                            '::-webkit-scrollbar': {
                                display: 'none',
                            },
                        }}
                    >
                        <Tabs
                            value={currentTab}
                            onChange={(_, tab) => setCurrentTab(tab)}
                            centered
                            variant="fullWidth"
                            indicatorColor="secondary"
                            textColor="inherit"
                            sx={{
                                bgcolor: '#007fff',
                                color: '#fff',
                                position: 'sticky',
                                top: 0,
                                zIndex: 1000,
                            }}
                        >
                            {/* /TODO diffrerent name if teacher? */}
                            <Tab label="Instrukcje od nauczyciela" />
                            <Tab label="Informacje o używanych modułach" />
                        </Tabs>

                        <Grid container sx={{ p: 2 }}>
                            {currentTab === 0 && (
                                <Box>
                                    {activity.instructions.map(({ header, body }, idx) => (
                                        <Paper
                                            key={idx}
                                            sx={{ mb: idx < activity.instructions.length - 1 ? 2 : 0, p: 2 }}
                                        >
                                            <Typography variant="h5">{header}</Typography>
                                            <Divider sx={{ mb: 2 }} />
                                            <Typography variant="body1">{body}</Typography>
                                        </Paper>
                                    ))}
                                </Box>
                            )}

                            {currentTab === 1 && <Grid container>TODO</Grid>}
                        </Grid>
                    </CardContent>
                    <Grid container sx={{ justifyContent: 'flex-end', p: 1 }}>
                        <Button onClick={onClose}>{t('course.activity.linksInfo.closeButton')}</Button>
                    </Grid>
                </Card>
            </AbsolutelyCentered>
        </Modal>
    );
};
