import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    Box,
    Button,
    Card,
    CardContent,
    Divider,
    Grid,
    Link,
    Modal,
    Typography,
} from '@mui/material';
import { ActivityDetailsDto } from 'api';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { stylesColumn } from 'common/styles';
import { CourseContext } from 'pages/Course/CourseView';
import { useContext } from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    activity: ActivityDetailsDto;
    open: boolean;
    onClose: () => void;
};

export const LinksModal = ({ activity, open, onClose }: Props) => {
    const { t } = useTranslation();
    const course = useContext(CourseContext);

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
                                {t('course.activity.linksInfo.info', {
                                    courseName: course?.name,
                                    activityName: activity.name,
                                })}
                            </Typography>
                            <Divider />
                        </Box>

                        <Grid container sx={{ p: 2 }}>
                            <Grid item xs={12}>
                                {activity.activityModules.map((activityModule) => (
                                    <Accordion sx={{ mb: 2, boxShadow: 3 }} key={activityModule.id}>
                                        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                                            <Box
                                                sx={{ cursor: 'default', pr: 4 }}
                                                onClick={(e) => e.stopPropagation()}
                                            >
                                                <Typography
                                                    sx={{ textTransform: 'capitalize' }}
                                                    component="h3"
                                                    variant="h6"
                                                >
                                                    {/* {activityModule.module.name} */}
                                                    Jupyter
                                                </Typography>
                                                {/* TODO */}
                                                {activityModule.connectionDetails.length === 0 && (
                                                    <Typography>
                                                        {' '}
                                                        Linki nie są jeszcze dostępne, odśwież stronę przed
                                                        rozpoczęciem zajęć
                                                    </Typography>
                                                )}
                                                {activityModule.connectionDetails.map(
                                                    ({ url, serviceName }) => (
                                                        <Link
                                                            key={serviceName}
                                                            target="_blank"
                                                            rel="noopener"
                                                            href={url}
                                                        >
                                                            {/* TODO */}
                                                            {url ?? 'Link nie jest jeszcze dostępny'}
                                                        </Link>
                                                    )
                                                )}
                                                <Box
                                                    sx={{
                                                        ...stylesColumn,
                                                        userSelect: 'text',
                                                        ':hover': { cursor: 'text' },
                                                    }}
                                                >
                                                    {/* TODO use more advanced/flexible format // fix this xD*/}
                                                    {activityModule.connectionDetails
                                                        .flatMap(
                                                            ({ connectionInstruction }) =>
                                                                connectionInstruction?.split('\n') ?? ''
                                                        )
                                                        .map((line, idx) => (
                                                            <Box key={idx}>
                                                                <Typography>{line}</Typography>
                                                            </Box>
                                                        ))}
                                                </Box>
                                            </Box>
                                        </AccordionSummary>
                                        <AccordionDetails sx={{ mt: -1 }}>
                                            <Typography component="h4" variant="h6">
                                                {t('course.activity.linksInfo.connectionInstruction')}
                                            </Typography>
                                            <Divider />
                                            <Box sx={{ mt: 1 }}>
                                                {/* TODO  */}
                                                {activityModule.connectionDetails
                                                    .flatMap(
                                                        ({ connectionInstruction }) =>
                                                            connectionInstruction?.split('\n') ?? ''
                                                    )
                                                    .map((line, idx) => (
                                                        <Box key={idx}>
                                                            <Typography variant="body2">{line}</Typography>
                                                        </Box>
                                                    ))}
                                            </Box>
                                        </AccordionDetails>
                                    </Accordion>
                                ))}
                            </Grid>
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
