import { Box, Button, Typography } from '@mui/material';
import { SendResetPasswordEmailRequest } from 'api';
import { stylesColumnCenteredHorizontal, stylesRowWithSpaceBetweenItems } from 'common/styles';
import { Form, Formik } from 'formik';
import { useTranslation } from 'react-i18next';
import { StyledInputField } from './StyledInputField';

type Props = {
    onRemind: (sendResetPasswordEmail: SendResetPasswordEmailRequest) => void;
    onSwithToLogin: () => void;
    buttonsDisabled: boolean;
};

export const ForgotPasswordForm = ({ onRemind, onSwithToLogin, buttonsDisabled }: Props) => {
    const { t } = useTranslation();

    return (
        <Formik initialValues={{ email: '' }} onSubmit={(values) => onRemind(values)}>
            {() => (
                <Form>
                    <Box
                        sx={{
                            marginTop: 8,
                            ...stylesColumnCenteredHorizontal,
                        }}
                    >
                        <Typography component="h1" variant="h5">
                            {t('login.forgot.header')}
                        </Typography>
                        <StyledInputField name="email" type="email" i18nLabel="login.email" autofocus />
                        <Box sx={{ ...stylesRowWithSpaceBetweenItems, width: '100%', mt: 2 }}>
                            <Button onClick={() => onSwithToLogin()} disabled={buttonsDisabled}>
                                {t('login.forgot.backToLogin')}
                            </Button>
                            <Button
                                type="submit"
                                disabled={buttonsDisabled}
                                variant="contained"
                                sx={{ px: 4 }}
                            >
                                {t('login.forgot.sendButton')}
                            </Button>
                        </Box>
                    </Box>
                </Form>
            )}
        </Formik>
    );
};
