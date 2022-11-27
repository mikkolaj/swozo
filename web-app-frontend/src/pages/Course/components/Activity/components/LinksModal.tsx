import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    Box,
    Button,
    Divider,
    Grid,
    Link,
    Typography,
} from '@mui/material';
import blue from '@mui/material/colors/blue';
import { ActivityDetailsDto, ActivityModuleDetailsDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { ScrollableCenteredModal } from 'common/Styled/ScrollableCenteredModal';
import { stylesColumnCenteredVertical } from 'common/styles';
import { useMeQuery } from 'hooks/query/useMeQuery';
import _ from 'lodash';
import { CourseContext } from 'pages/Course/CourseView';
import { setLinkDeliveryConfirmed, updateCourseCache } from 'pages/Course/utils';
import { useContext } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useDispatch } from 'react-redux';
import { Link as RouterLink } from 'react-router-dom';
import { toast } from 'react-toastify';
import { triggerError } from 'services/features/error/errorSlice';
import { isSame } from 'utils/roles';
import { PageRoutes } from 'utils/routes';
import { getTranslated } from 'utils/util';
import './connectionInstructions.css';

type Props = {
    activity: ActivityDetailsDto;
    open: boolean;
    onClose: () => void;
};

export const LinksModal = ({ activity, open, onClose }: Props) => {
    const { i18n, t } = useTranslation();
    const course = useContext(CourseContext);
    const queryClient = useQueryClient();
    const dispatch = useDispatch();
    const { me } = useMeQuery();

    const confirmLinkDeliveryMutation = useMutation(
        (activityModule: ActivityModuleDetailsDto) =>
            getApis().activitiesApi.confirmLinkCanBeDeliveredToStudents({
                activityModuleId: activityModule.id,
            }),
        {
            onSuccess: (_, activityModule) => {
                if (!course) return;
                updateCourseCache(queryClient, setLinkDeliveryConfirmed(course, activity, activityModule));
                toast.success(t('toast.linkDeliveryConfirmed'));
            },
            onError: () => {
                dispatch(triggerError({ message: t('error.tryAgain'), autoClose: true }));
            },
        }
    );

    return (
        <ScrollableCenteredModal
            open={open}
            onClose={onClose}
            header={t('course.activity.linksInfo.info', {
                courseName: course?.name,
                activityName: activity.name,
            })}
            closeBtn={t('course.activity.linksInfo.closeButton')}
        >
            <Grid container sx={{ p: 2 }}>
                <Grid item xs={12} sx={{ width: '100%' }}>
                    {activity.activityModules.map((activityModule) => (
                        <Accordion sx={{ mb: 2, boxShadow: 3 }} key={activityModule.id}>
                            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                                <Box sx={{ cursor: 'default', pr: 4 }} onClick={(e) => e.stopPropagation()}>
                                    <Typography variant="h6">
                                        {activityModule.serviceModule.name + ' ('}
                                        <RouterLink
                                            target="_blank"
                                            rel="noopener"
                                            to={PageRoutes.Service(activityModule.serviceModule.serviceName)}
                                            style={{ textDecoration: 'none', color: blue[700] }}
                                        >
                                            {activityModule.serviceModule.serviceName}
                                        </RouterLink>
                                        {' )'}
                                    </Typography>
                                    {activityModule.connectionDetails.length === 0 && (
                                        <Typography>
                                            {t('course.activity.linksInfo.linksUnavailable')}
                                        </Typography>
                                    )}
                                    {isSame(me, course?.teacher) && (
                                        <Box>
                                            {!activityModule.linkConfirmationRequired ||
                                            activityModule.linkConfirmed ? (
                                                <Typography>
                                                    {t('course.activity.linksInfo.linkDeliveryConfirmed')}
                                                </Typography>
                                            ) : (
                                                <Box>
                                                    <Typography>
                                                        {t(
                                                            'course.activity.linksInfo.linkConfirmationRequired'
                                                        )}
                                                    </Typography>
                                                    <Button
                                                        sx={{ ml: -1 }}
                                                        onClick={() =>
                                                            confirmLinkDeliveryMutation.mutate(activityModule)
                                                        }
                                                    >
                                                        {t('course.activity.linksInfo.confirmLinkDelivery')}
                                                    </Button>
                                                </Box>
                                            )}
                                        </Box>
                                    )}
                                    {activityModule.connectionDetails.map(({ url }) => (
                                        <Box
                                            key={url}
                                            sx={{
                                                ...stylesColumnCenteredVertical,
                                            }}
                                        >
                                            <Link target="_blank" rel="noopener" href={url}>
                                                {`${url.slice(0, 90)}${url.length > 90 ? '...' : ''}`}
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
                                                <span
                                                    dangerouslySetInnerHTML={{
                                                        __html: getTranslated(
                                                            i18n,
                                                            _.mapValues(
                                                                connectionInstructions,
                                                                (v) => v.untrustedPossiblyDangerousHtml
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
        </ScrollableCenteredModal>
    );
};
