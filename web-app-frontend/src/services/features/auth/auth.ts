import { store } from 'services/store';
import { ModalId, openModal } from '../modal/modalSlice';
import { isCloseToExpire, isTokenExpired, logout, refreshToken } from './authSlice';

export const getAccessToken = async (): Promise<string> => {
    const auth = store.getState().auth;

    if (auth.isLoggedIn && auth.authData) {
        if (isTokenExpired(auth.authData)) {
            store.dispatch(logout());
            store.dispatch(
                openModal({
                    modalProps: { id: ModalId.SESSION_EXPIRED, allowClose: true },
                    forcePrecedence: true,
                })
            );
        } else {
            if (isCloseToExpire(auth.authData.expiresIn)) {
                store.dispatch(refreshToken());
            }

            return auth.authData.accessToken;
        }
    }

    return new Promise((_, rej) => rej('access token not found or expired'));
};
