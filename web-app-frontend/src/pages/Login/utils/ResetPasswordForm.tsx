import { Box, Button, Typography } from '@mui/material';
import { stylesColumnCenteredHorizontal } from 'common/styles';
import { Form, Formik, FormikProps } from 'formik';
import { RefObject } from 'react';
import { useTranslation } from 'react-i18next';
import { StyledInputField } from './StyledInputField';

export type ResetFormValues = {
    password: string;
    token: string;
    repeatPassword: string;
};

type Props = {
    onReset: (token: string, password: string) => void;
    formRef?: RefObject<FormikProps<ResetFormValues>>;
    buttonDisabled: boolean;
};

export const ResetPasswordForm = ({ onReset, formRef, buttonDisabled }: Props) => {
    const { t } = useTranslation();

    return (
        <Formik
            initialValues={{ token: '', password: '', repeatPassword: '' }}
            innerRef={formRef}
            onSubmit={({ token, password }) => onReset(token, password)}
            validate={(values) => {
                if (values.password !== values.repeatPassword) {
                    return {
                        repeatPassword: t('login.reset.error.passwordsDontMatch'),
                    };
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
                            {t('login.reset.header')}
                        </Typography>
                        <StyledInputField name="token" type="text" i18nLabel="login.reset.token" />
                        <StyledInputField name="password" type="password" i18nLabel="login.reset.password" />
                        <StyledInputField
                            name="repeatPassword"
                            type="password"
                            i18nLabel="login.reset.repeatPassword"
                        />

                        <Button
                            type="submit"
                            disabled={buttonDisabled}
                            variant="contained"
                            fullWidth
                            sx={{ mt: 2 }}
                        >
                            {t('login.reset.sendButton')}
                        </Button>
                    </Box>
                </Form>
            )}
        </Formik>
    );
};
