import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    Box,
    Button,
    Card,
    CardContent,
    Grid,
    Link,
    Modal,
    Typography,
} from '@mui/material';
import { AbsolutelyCentered } from 'common/Styled/AbsolutetlyCentered';
import { Bar } from 'common/Styled/Bar';
import { CourseContext } from 'pages/Course/CourseView';
import { useContext } from 'react';
import { useTranslation } from 'react-i18next';
import { Activity } from 'utils/mocks';

type Props = {
    activity: Activity;
    open: boolean;
    onClose: () => void;
};

export const LinksModal = ({ activity, open, onClose }: Props) => {
    const { t } = useTranslation();
    const course = useContext(CourseContext);

    return (
        <Modal sx={{ minWidth: 700, minHeight: 500 }} open={open} onClose={onClose}>
            <AbsolutelyCentered>
                <Card sx={{ borderRadius: 5, border: 'none', boxShadow: 3 }}>
                    <CardContent>
                        <Typography component="h1" variant="h6" gutterBottom>
                            {t('course.activity.linksInfo.info', {
                                courseName: course?.name,
                                activityName: activity.name,
                            })}
                        </Typography>
                        <Bar />
                        <Grid container>
                            <Grid item sx={{ mt: 5 }}>
                                {activity.links.map((link, idx) => {
                                    return (
                                        <Accordion sx={{ mb: 2, boxShadow: 3 }} key={idx}>
                                            <AccordionSummary
                                                expandIcon={<ExpandMoreIcon />}
                                                aria-controls="panel1a-content"
                                                id="panel1a-header"
                                            >
                                                <Box
                                                    sx={{ cursor: 'default' }}
                                                    onClick={(e) => e.stopPropagation()}
                                                >
                                                    <Typography component="h3" variant="h6">
                                                        {link.serviceName}
                                                    </Typography>
                                                    <Link target="_blank" rel="noopener" href={link.url}>
                                                        {link.url}
                                                    </Link>
                                                </Box>
                                            </AccordionSummary>
                                            <AccordionDetails sx={{ mt: -1 }}>
                                                <Typography variant="body2">{link.serviceInfo}</Typography>
                                            </AccordionDetails>
                                        </Accordion>
                                    );
                                })}
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
