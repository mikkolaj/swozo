import { Box, Button, Container, Typography } from '@mui/material';
import { InputField } from 'common/Input/InputField';
import { stylesColumnCenteredHorizontal } from 'common/styles';
import { Form, Formik } from 'formik';
import { useTranslation } from 'react-i18next';
import { useLocation, useNavigate } from 'react-router-dom';
import { login } from 'services/features/auth/authSlice';
import { useAppDispatch, useAppSelector } from 'services/store';
import { RedirectState } from 'utils/types';

export const Login = () => {
    const { t } = useTranslation();
    const location = useLocation();
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const authState = useAppSelector((state) => state.auth);

    return (
        <Container component="main" maxWidth="xs">
            <Formik
                initialValues={{ email: '', password: '' }}
                onSubmit={async (values) => {
                    await dispatch(login(values));
                    const { redirectTo } = (location.state as RedirectState) ?? { redirectTo: undefined };
                    if (redirectTo) {
                        navigate(redirectTo);
                    }
                }}
            >
                {() => (
                    <Form>
                        <Box
                            sx={{
                                marginTop: 8,
                                ...stylesColumnCenteredHorizontal,
                            }}
                        >
                            <Typography component="h1" variant="h5">
                                {t('login.header')}
                            </Typography>
                            <InputField
                                name="email"
                                type="text" // TODO make it email, text for easier testing (no @ required)
                                textFieldProps={{
                                    required: true,
                                    fullWidth: true,
                                    autoFocus: true,
                                    variant: 'outlined',
                                }}
                                wrapperSx={{
                                    mt: 3,
                                    width: '100%',
                                }}
                                i18nLabel="login.email"
                            />
                            <InputField
                                name="password"
                                type="password"
                                textFieldProps={{
                                    required: true,
                                    fullWidth: true,
                                    variant: 'outlined',
                                }}
                                wrapperSx={{
                                    mt: 2,
                                    width: '100%',
                                }}
                                i18nLabel="login.password"
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
