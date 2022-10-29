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
import { InstructionView } from 'common/Styled/InstructionView';
import { stylesColumnCenteredVertical } from 'common/styles';
import _ from 'lodash';
import { CourseContext } from 'pages/Course/CourseView';
import { useContext } from 'react';
import { useTranslation } from 'react-i18next';
import { getTranslated } from 'utils/util';

type Props = {
    activity: ActivityDetailsDto;
    open: boolean;
    onClose: () => void;
};

export const LinksModal = ({ activity, open, onClose }: Props) => {
    const { i18n, t } = useTranslation();
    const course = useContext(CourseContext);

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
                                                    {t('course.activity.linksInfo.title', {
                                                        serviceName: _.capitalize(
                                                            activityModule.serviceModule.serviceName
                                                        ),
                                                        serviceModuleName: activityModule.serviceModule.name,
                                                    })}
                                                </Typography>
                                                {activityModule.connectionDetails.length === 0 && (
                                                    <Typography>
                                                        {t('course.activity.linksInfo.linksUnavailable')}
                                                    </Typography>
                                                )}
                                                {activityModule.connectionDetails.map(({ url }) => (
                                                    <Box key={url} sx={stylesColumnCenteredVertical}>
                                                        <Link target="_blank" rel="noopener" href={url}>
                                                            {url}
                                                        </Link>
                                                    </Box>
                                                ))}
                                            </Box>
                                        </AccordionSummary>
                                        <AccordionDetails sx={{ mt: -1 }}>
                                            <Typography component="h4" variant="h6">
                                                {t('course.activity.linksInfo.connectionInstruction')}
                                            </Typography>
                                            <Divider />
                                            <Box sx={{ mt: 1 }}>
                                                {activityModule.connectionDetails.map(
                                                    ({ connectionInstructions }, idx) => (
                                                        <Box key={idx} sx={{ mt: 2 }}>
                                                            <InstructionView
                                                                instruction={{
                                                                    untrustedPossiblyDangerousHtml:
                                                                        getTranslated(
                                                                            i18n,
                                                                            _.mapValues(
                                                                                connectionInstructions,
                                                                                (v) =>
                                                                                    v.untrustedPossiblyDangerousHtml
                                                                            )
                                                                        ),
                                                                }}
                                                            />
                                                        </Box>
                                                    )
                                                )}
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
