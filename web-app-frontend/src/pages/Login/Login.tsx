import { Box, Button, Container, Typography } from '@mui/material';
import { InputField } from 'common/Input/InputField';
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
                {() => (
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
                            <InputField
                                name="email"
                                textFieldProps={{
                                    required: true,
                                    fullWidth: true,
                                    autoFocus: true,
                                    autoComplete: 'email',
                                    variant: 'outlined',
                                }}
                                wrapperSx={{
                                    mt: 3,
                                    width: '100%',
                                }}
                                type="email"
                                labelPath="login.email"
                            />
                            <InputField
                                name="password"
                                textFieldProps={{
                                    required: true,
                                    fullWidth: true,
                                    autoComplete: 'current-password',
                                    variant: 'outlined',
                                }}
                                wrapperSx={{
                                    mt: 2,
                                    width: '100%',
                                }}
                                type="password"
                                labelPath="login.password"
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
