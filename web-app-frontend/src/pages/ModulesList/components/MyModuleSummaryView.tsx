import DeleteIcon from '@mui/icons-material/Delete';
import EditIcon from '@mui/icons-material/Edit';
import { Box, Button, Card, CardContent, Grid, Typography } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { stylesRowCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { formatDateTime } from 'utils/util';

type Props = {
    moduleSummary: ServiceModuleSummaryDto;
    onDelete: () => void;
};

export const ModuleSummaryView = ({ moduleSummary, onDelete }: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <Card sx={{ boxShadow: 3 }}>
            <CardContent>
                <Grid container>
                    <Grid item xs={8}>
                        <LinkedTypography
                            variant="h4"
                            to={PageRoutes.MyModule(moduleSummary.id)}
                            sx={{ overflowX: 'hidden', textOverflow: 'ellipsis' }}
                            text={moduleSummary.name}
                        />
                        <Typography
                            variant="body1"
                            sx={{
                                opacity: 0.8,
                                minWidth: '40%',
                                width: 'fit-content',
                                borderBottom: '1px solid rgba(0,0,0, 0.3)',
                            }}
                        >
                            #{_.capitalize(moduleSummary.subject)}
                        </Typography>
                    </Grid>
                    <Grid item xs={4} sx={{ textAlign: 'right' }}>
                        <Typography variant="h6" component="div">
                            {t('myModules.module.creationDate')}
                        </Typography>
                        <Typography sx={{ mt: -1 }} variant="h6" component="div">
                            {formatDateTime(moduleSummary.createdAt)}
                        </Typography>
                    </Grid>
                    <Grid item xs={4} sx={{ ...stylesRowCenteredVertical, mt: 2 }}>
                        <Typography variant="body2">
                            {t('myModules.module.usedBy', {
                                activitiesCount: moduleSummary.usedInActivitiesCount,
                            })}
                        </Typography>
                    </Grid>
                    <Grid item xs={8} sx={{ mt: 2 }}>
                        <Box sx={stylesRowWithItemsAtTheEnd}>
                            {moduleSummary.usedInActivitiesCount === 0 && (
                                <Button
                                    startIcon={<DeleteIcon />}
                                    variant="outlined"
                                    onClick={() => onDelete()}
                                    sx={{ marginRight: 1 }}
                                >
                                    {t('myModules.module.buttons.delete')}
                                </Button>
                            )}
                            <Button
                                startIcon={<EditIcon />}
                                variant="outlined"
                                onClick={() => navigate(PageRoutes.EditModule(moduleSummary.id))}
                                sx={{ marginRight: 1 }}
                            >
                                {t('myModules.module.buttons.edit')}
                            </Button>
                            <Button
                                variant="contained"
                                onClick={() => navigate(PageRoutes.MyModule(moduleSummary.id))}
                            >
                                {t('myModules.module.buttons.details')}
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            </CardContent>
        </Card>
    );
};
