import { Box, Button, Card, CardContent, Divider, Grid, Paper, Typography } from '@mui/material';
import { Calendar } from 'common/Calendar/Calendar';
import { useNavigate } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';

export const Home = () => {
    const navigate = useNavigate();

    // TODO this is just a demo version
    return (
        <Box sx={{ padding: 0, marginX: '200px', marginTop: -5 }}>
            <Grid container spacing={10} sx={{ marginTop: 0 }}>
                <Grid item xs={6}>
                    <Card
                        sx={{
                            position: 'relative',
                            shadow: 3,
                        }}
                    >
                        <Calendar />
                        <Divider />
                        <Box sx={{ p: 2 }}>
                            <Typography variant="h6">Nadchodzące aktywności</Typography>
                            <Paper
                                sx={{
                                    p: 1,
                                    m: 2,
                                    boxShadow: 2,
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'space-between',
                                }}
                            >
                                <Typography>Programowanie w języku Python</Typography>
                                <Typography>14:40 25.03.2022</Typography>
                            </Paper>
                            <Paper
                                sx={{
                                    p: 1,
                                    m: 2,
                                    boxShadow: 2,
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'space-between',
                                }}
                            >
                                <Typography>Programowanie w języku Python</Typography>
                                <Typography>14:40 25.03.2022</Typography>
                            </Paper>
                            <Paper
                                sx={{
                                    p: 1,
                                    m: 2,
                                    boxShadow: 2,
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'space-between',
                                }}
                            >
                                <Typography>Programowanie w języku Python</Typography>
                                <Typography>14:40 25.03.2022</Typography>
                            </Paper>
                            <Button sx={{ display: 'block', margin: 'auto' }}>Zobacz wszystkie</Button>
                        </Box>
                    </Card>
                </Grid>
                <Grid item xs={6}>
                    <Card sx={{ shadow: 3, position: 'relative' }}>
                        <CardContent>
                            <Typography variant="h6">Ostatnio aktywne kursy</Typography>
                            <Divider />
                            <Paper
                                sx={{
                                    p: 1,
                                    m: 2,
                                    boxShadow: 2,
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'space-between',
                                }}
                            >
                                <Typography>Systemy operacyjne</Typography>
                                <Typography>Odwołano zajęcia</Typography>
                            </Paper>
                            <Paper
                                sx={{
                                    p: 1,
                                    m: 2,
                                    boxShadow: 2,
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'space-between',
                                }}
                            >
                                <Typography>Programowanie w języku Python</Typography>
                                <Typography>Nowe ogłoszenia</Typography>
                            </Paper>
                            <Paper
                                sx={{
                                    p: 1,
                                    m: 2,
                                    boxShadow: 2,
                                    display: 'flex',
                                    flexDirection: 'row',
                                    justifyContent: 'space-between',
                                }}
                            >
                                <Typography>Programowanie w języku Python</Typography>
                                <Typography>Nowe ogłoszenia</Typography>
                            </Paper>
                            <Button
                                onClick={() => navigate(PageRoutes.MY_COURSES)}
                                sx={{ display: 'block', margin: 'auto' }}
                            >
                                Zobacz wszystkie kursy
                            </Button>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>
        </Box>
    );
};
