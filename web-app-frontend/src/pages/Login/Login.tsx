import { Box, Button, Container, TextField, Typography } from '@mui/material';
import { Form, Formik } from 'formik';
import { useTranslation } from 'react-i18next';
import { login } from 'services/features/auth/authSlice';
import { useAppDispatch, useAppSelector } from 'services/store';

const Login = () => {
    const dispatch = useAppDispatch();
    const authState = useAppSelector((state) => state.auth);
    const { t } = useTranslation();

    return (
        <Container component="main" maxWidth="xs">
            <Formik
                initialValues={{ email: '', password: '' }}
                onSubmit={(values) => {
                    dispatch(login(values));
                }}
            >
                {({ values, handleChange }) => (
                    <Form>
                        <Box
                            sx={{
                                marginTop: 8,
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                            }}
                        >
                            <Typography component="h1" variant="h5">
                                {t('login.header')}
                            </Typography>
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                label={t('login.email')}
                                name="email"
                                autoComplete="email"
                                value={values.email}
                                onChange={handleChange}
                                autoFocus
                            />
                            <TextField
                                margin="normal"
                                required
                                fullWidth
                                name="password"
                                label={t('login.password')}
                                type="password"
                                autoComplete="current-password"
                                value={values.password}
                                onChange={handleChange}
                            />
                            <Button
                                type="submit"
                                disabled={authState.isFetching}
                                fullWidth
                                variant="contained"
                                sx={{ mt: 3, mb: 2 }}
                            >
                                {t('login.button')}
                            </Button>
                        </Box>
                    </Form>
                )}
            </Formik>
            {/* TODO error handling */}
            {authState.errors && authState.errors.map((error, idx) => <div key={idx}>{error}</div>)}
        </Container>
    );
};

export default Login;
