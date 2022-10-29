import { Box, Button, Card, CardContent, Grid, Typography } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { stylesColumnCenteredVertical, stylesRowWithItemsAtTheEnd } from 'common/styles';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';
import { formatDateTime } from 'utils/util';

type Props = {
    moduleSummary: ServiceModuleSummaryDto;
};

export const PublicModuleSummaryView = ({ moduleSummary }: Props) => {
    const { t } = useTranslation();
    const navigate = useNavigate();

    return (
        <Card sx={{ boxShadow: 3 }}>
            <CardContent>
                <Grid container>
                    <Grid item xs={8}>
                        <LinkedTypography
                            variant="h4"
                            to={PageRoutes.PublicModule(moduleSummary.id)}
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
                    <Grid
                        item
                        xs={8}
                        sx={{
                            ...stylesColumnCenteredVertical,
                            mt: 2,
                            overflow: 'hidden',
                            whiteSpace: 'nowrap',
                            textOverflow: 'ellipsis ellipsis',
                        }}
                    >
                        <Typography variant="body1">
                            {t('publicModules.author', { email: moduleSummary.creator.email })}
                        </Typography>
                        <Typography variant="body2" noWrap>
                            {t('publicModules.description', { description: moduleSummary.description })}
                        </Typography>
                    </Grid>
                    <Grid item xs={4} sx={{ mt: 2 }}>
                        <Box sx={stylesRowWithItemsAtTheEnd}>
                            <Button
                                variant="contained"
                                onClick={() => navigate(PageRoutes.PublicModule(moduleSummary.id))}
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
