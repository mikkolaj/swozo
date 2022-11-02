import { Container } from '@mui/material';
import { ResetPasswordRequest, SendResetPasswordEmailRequest } from 'api';
import { ApiError, ErrorType } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import { FormikProps } from 'formik';
import { buildErrorHandler, useApiErrorHandling } from 'hooks/useApiErrorHandling';
import { useEffect, useRef, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useLocation, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { clearAuthError, login } from 'services/features/auth/authSlice';
import { triggerError } from 'services/features/error/errorSlice';
import { closeModal, ModalId, openModal } from 'services/features/modal/modalSlice';
import { useAppDispatch, useAppSelector } from 'services/store';
import { RedirectState } from 'utils/types';
import { handleFlatFormError } from 'utils/util';
import { ForgotPasswordForm } from './utils/ForgotPasswordForm';
import { LoginForm } from './utils/LoginForm';
import { ResetFormValues, ResetPasswordForm } from './utils/ResetPasswordForm';

type Mode = 'login' | 'forgot' | 'reset';

export const LoginView = () => {
    const { t } = useTranslation();
    const location = useLocation();
    const navigate = useNavigate();
    const dispatch = useAppDispatch();
    const queryClient = useQueryClient();
    const authState = useAppSelector((state) => state.auth);
    const [mode, setMode] = useState<Mode>('login');
    const [emailForReset, setEmailForReset] = useState<string>('');
    const resetPasswordFormRef = useRef<FormikProps<ResetFormValues>>(null);

    const { pushApiError, isApiErrorSet, removeApiError } = useApiErrorHandling({
        [ErrorType.INVALID_CREDENTIALS]: buildErrorHandler(() => {
            dispatch(triggerError({ message: t('login.invalidCredentials') }));
        }, false),
        [ErrorType.USER_NOT_FOUND]: buildErrorHandler(() => {
            dispatch(triggerError({ message: t('login.forgot.userNotFound') }));
        }, false),
        [ErrorType.FAILED_TO_SEND_EMAIL]: buildErrorHandler(() => {
            dispatch(triggerError({ message: t('login.forgot.failedToSendEmail') }));
        }, false),
    });

    useEffect(() => {
        if (authState.error && !isApiErrorSet(authState.error)) {
            pushApiError(authState.error);
            dispatch(clearAuthError());
        }
    }, [authState, pushApiError, isApiErrorSet, dispatch]);

    const sendResetPasswordEmailMutation = useMutation(
        (sendResetPasswordEmailRequest: SendResetPasswordEmailRequest) => {
            dispatch(
                openModal({
                    modalProps: {
                        id: ModalId.REMIND_PASSWORD_IN_PROGRESS,
                        allowClose: false,
                        textLines: [t('login.forgot.modal')],
                    },
                })
            );
            return getApis().authApi.sendResetPasswordEmail({ sendResetPasswordEmailRequest });
        },
        {
            onSuccess: (_, { email }) => {
                setEmailForReset(email);
                dispatch(
                    openModal({
                        modalProps: {
                            id: ModalId.REMIND_PASSWORD_EMAIL_SENT,
                            allowClose: true,
                            textLines: [t('login.forgot.emailSent')],
                        },
                        forcePrecedence: true,
                    })
                );
                setMode('reset');
            },
            onError: pushApiError,
            onSettled: () => {
                dispatch(closeModal(ModalId.REMIND_PASSWORD_IN_PROGRESS));
            },
        }
    );

    const resetPasswordMutation = useMutation(
        (resetPasswordRequest: ResetPasswordRequest) =>
            getApis().authApi.resetPassword({
                resetPasswordRequest,
            }),
        {
            onSuccess: () => {
                toast.success(t('toast.passwordChanged'));
                setMode('login');
            },
            onError: (err: ApiError) => {
                handleFlatFormError(t, resetPasswordFormRef.current, err, 'login.reset.error', pushApiError);
            },
        }
    );

    return (
        <Container component="main" maxWidth="xs">
            {mode === 'login' && (
                <LoginForm
                    onLogin={async (loginRequest) => {
                        queryClient.removeQueries();
                        removeApiError({ errorType: ErrorType.INVALID_CREDENTIALS });
                        await dispatch(login(loginRequest));
                        const { redirectTo } = (location.state as RedirectState) ?? { redirectTo: undefined };
                        if (redirectTo) {
                            navigate(redirectTo);
                        }
                    }}
                    onForgotPassword={() => setMode('forgot')}
                    buttonsDisabled={authState.isFetching}
                />
            )}
            {mode === 'forgot' && (
                <ForgotPasswordForm
                    onRemind={sendResetPasswordEmailMutation.mutate}
                    onSwithToLogin={() => setMode('login')}
                    buttonsDisabled={sendResetPasswordEmailMutation.isLoading}
                />
            )}
            {mode === 'reset' && (
                <ResetPasswordForm
                    formRef={resetPasswordFormRef}
                    onReset={(token, password) => {
                        if (emailForReset) {
                            resetPasswordMutation.mutate({ email: emailForReset, token, password });
                        }
                    }}
                    buttonDisabled={resetPasswordMutation.isLoading}
                />
            )}
        </Container>
    );
};
