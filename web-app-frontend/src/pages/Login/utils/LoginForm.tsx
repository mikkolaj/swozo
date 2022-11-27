import { Box, Button, Typography } from '@mui/material';
import { LoginRequest } from 'api';
import { stylesColumnCenteredHorizontal, stylesRowWithSpaceBetweenItems } from 'common/styles';
import { Form, Formik } from 'formik';
import { useTranslation } from 'react-i18next';
import { StyledInputField } from './StyledInputField';

type Props = {
    onLogin: (loginRequest: LoginRequest) => void;
    onForgotPassword: () => void;
    buttonsDisabled: boolean;
};

export const LoginForm = ({ onLogin, onForgotPassword, buttonsDisabled }: Props) => {
    const { t } = useTranslation();

    return (
        <Box>
            <Formik initialValues={{ email: '', password: '' }} onSubmit={(values) => onLogin(values)}>
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
                            <StyledInputField name="email" type="email" i18nLabel="login.email" autofocus />
                            <StyledInputField name="password" type="password" i18nLabel="login.password" />

                            <Box sx={{ ...stylesRowWithSpaceBetweenItems, width: '100%', mt: 2 }}>
                                <Button onClick={() => onForgotPassword()} disabled={buttonsDisabled}>
                                    {t('login.forgotPassword')}
                                </Button>
                                <Button
                                    type="submit"
                                    disabled={buttonsDisabled}
                                    variant="contained"
                                    sx={{ px: 4 }}
                                >
                                    {t('login.loginButton')}
                                </Button>
                            </Box>
                        </Box>
                    </Form>
                )}
            </Formik>
            <Box
                sx={{
                    position: 'fixed',
                    left: '50%',
                    bottom: 20,
                    transform: 'translateX(-50%)',
                    textAlign: 'center',
                }}
            >
                <Typography variant="h6">{t('login.tip')}</Typography>
            </Box>
        </Box>
    );
};
