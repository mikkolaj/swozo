import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { AuthDetailsDto, AuthDetailsDtoRolesEnum, LoginRequest } from 'api';
import { ApiError } from 'api/errors';
import { getApis } from 'api/initialize-apis';
import dayjs from 'dayjs';
import { AppDispatch, RootState } from 'services/store';
import { hasRole } from 'utils/roles';
import { clearLocalStorage, loadFromLocalStorage, persistWithLocalStorage } from 'utils/util';

const LOCAL_STORAGE_AUTH_KEY = 'JWT';
const LOCAL_STORAGE_ROLE_PREF_KEY = 'ROLE_PREF';
export const CLOSE_TO_EXPIRE_MINUTES = 1;

export type AuthState = {
    authData?: AuthDetailsDto;
    rolePreference?: AuthDetailsDtoRolesEnum;
    isLoggedIn: boolean;
    isFetching: boolean;
    error?: ApiError;
};

const isExpired = (unixNanos: number) => unixNanos * 1000 <= new Date().getTime();

export function isTokenExpired(authData: AuthDetailsDto) {
    return isExpired(authData.expiresIn);
}

export const isCloseToExpire = (expireTimeUnixNanos: number) =>
    expireTimeUnixNanos * 1000 <= dayjs().add(CLOSE_TO_EXPIRE_MINUTES, 'minutes').toDate().getTime();

function canHaveRolePreference(authData?: AuthDetailsDto, pref?: AuthDetailsDtoRolesEnum): boolean {
    if (!pref) return true;

    return hasRole(authData, pref);
}

function getDefaultRolePreference(authData: AuthDetailsDto): AuthDetailsDtoRolesEnum | undefined {
    if (hasRole(authData, AuthDetailsDtoRolesEnum.Teacher, AuthDetailsDtoRolesEnum.TechnicalTeacher))
        return AuthDetailsDtoRolesEnum.Teacher;
}

function buildInitialState(): AuthState {
    const authData = loadFromLocalStorage<AuthDetailsDto>(LOCAL_STORAGE_AUTH_KEY);
    const rolePreference = loadFromLocalStorage<AuthDetailsDtoRolesEnum>(LOCAL_STORAGE_ROLE_PREF_KEY);
    let isLoggedIn = false;

    if (authData) {
        if (!isTokenExpired(authData)) {
            isLoggedIn = true;
        } else {
            clearLocalStorage(LOCAL_STORAGE_AUTH_KEY);
            isLoggedIn = false;
        }
    }

    return {
        authData,
        isLoggedIn,
        isFetching: false,
        rolePreference: canHaveRolePreference(authData, rolePreference) ? rolePreference : undefined,
    };
}

const handleAuthResponse = createAsyncThunk<
    unknown,
    { resp: Promise<AuthDetailsDto>; refreshMode: boolean },
    { dispatch: AppDispatch }
>('auth/handleAuthResponse', async ({ resp, refreshMode }, { dispatch }) => {
    dispatch(setFetching(true));
    try {
        const authDetails = await resp;
        persistWithLocalStorage(LOCAL_STORAGE_AUTH_KEY, authDetails);
        dispatch(receiveAuthData(authDetails));

        let rolePreference = loadFromLocalStorage<AuthDetailsDtoRolesEnum>(LOCAL_STORAGE_ROLE_PREF_KEY);
        if (!rolePreference || !canHaveRolePreference(authDetails, rolePreference)) {
            rolePreference = getDefaultRolePreference(authDetails);
        }

        if (rolePreference) {
            dispatch(receiveRolePref(rolePreference));
        }
    } catch (err) {
        if (!refreshMode) dispatch(receiveAuthError(err as ApiError));
    } finally {
        dispatch(setFetching(false));
    }
});

export const login = createAsyncThunk<unknown, LoginRequest, { dispatch: AppDispatch; state: RootState }>(
    'auth/login',
    async (loginRequest, { getState, dispatch }) => {
        if (getState().auth.isFetching) return;

        await dispatch(
            handleAuthResponse({ resp: getApis().authApi.login({ loginRequest }), refreshMode: false })
        );
    }
);

export const logout = createAsyncThunk<unknown, undefined, { dispatch: AppDispatch; state: RootState }>(
    'auth/logout',
    (_, { dispatch, getState }) => {
        const auth = getState().auth;
        if (auth.authData && !isTokenExpired(auth.authData)) {
            // try it async, if it fails ignore
            getApis()
                .userApi.logout({ refreshTokenDto: auth.authData.refreshTokenDto })
                .catch((_) => undefined);
        }

        clearLocalStorage(LOCAL_STORAGE_AUTH_KEY);
        dispatch(resetAuthData());
    }
);

export const refreshToken = createAsyncThunk<unknown, undefined, { dispatch: AppDispatch; state: RootState }>(
    'auth/refresh',
    async (_, { dispatch, getState }) => {
        const auth = getState().auth;
        if (!auth.authData || auth.isFetching) return;

        if (!isExpired(auth.authData.refreshTokenDto.expiresIn)) {
            dispatch(
                handleAuthResponse({
                    resp: getApis().authApi.refreshAccessToken({
                        refreshTokenDto: auth.authData.refreshTokenDto,
                    }),
                    refreshMode: true,
                })
            );
        }
    }
);

// TODO redirect if on page not-for-that-preference?
export const setRolePreference = createAsyncThunk<
    unknown,
    AuthDetailsDtoRolesEnum,
    { dispatch: AppDispatch; state: RootState }
>('auth/roles/preference', (role: AuthDetailsDtoRolesEnum, { getState, dispatch }) => {
    const auth = getState().auth;
    if (!auth || auth.rolePreference === role || !canHaveRolePreference(auth.authData, role)) return;

    persistWithLocalStorage(LOCAL_STORAGE_ROLE_PREF_KEY, role);
    dispatch(receiveRolePref(role));
});

export const authSlice = createSlice({
    name: 'auth',
    initialState: buildInitialState(),
    reducers: {
        receiveAuthData: (state: AuthState, action: PayloadAction<AuthDetailsDto>) => {
            state.authData = action.payload;
            state.isLoggedIn = true;
            state.error = undefined;
        },
        resetAuthData: (state: AuthState) => {
            state.authData = undefined;
            state.isLoggedIn = false;
            state.error = undefined; // maybe leave it
        },
        receiveRolePref: (state: AuthState, action: PayloadAction<AuthDetailsDtoRolesEnum>) => {
            state.rolePreference = action.payload;
        },
        clearAuthError: (state: AuthState) => {
            state.error = undefined;
        },
        receiveAuthError: (state: AuthState, action: PayloadAction<ApiError>) => {
            state.error = action.payload;
        },
        setFetching: (state: AuthState, action: PayloadAction<boolean>) => {
            state.isFetching = action.payload;
        },
    },
});

const { receiveAuthData, resetAuthData, receiveAuthError, setFetching, receiveRolePref } = authSlice.actions;

export const { clearAuthError } = authSlice.actions;

export default authSlice.reducer;
