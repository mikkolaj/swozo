import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { AuthData, AuthDataRolesEnum, LoginData } from 'api';
import { getApis } from 'api/initialize-apis';
import { AppDispatch, RootState } from 'services/store';
import { hasRole } from 'utils/roles';

const LOCAL_STORAGE_AUTH_KEY = 'JWT';
const LOCAL_STORAGE_ROLE_PREF_KEY = 'ROLE_PREF';

export function isTokenExpired(authData: AuthData) {
    return authData.expiresIn * 1000 <= new Date().getTime();
}

function persistAuthState(auth: AuthData): void {
    window.localStorage.setItem(LOCAL_STORAGE_AUTH_KEY, JSON.stringify(auth));
}

function clearAuthPersistence(): void {
    window.localStorage.removeItem(LOCAL_STORAGE_AUTH_KEY);
}

function canHaveRolePreference(authData: AuthData | null, pref?: AuthDataRolesEnum): boolean {
    if (!pref) return true;

    return hasRole(authData, pref);
}

export type AuthState = {
    authData: AuthData | null;
    rolePreference?: AuthDataRolesEnum;
    isLoggedIn: boolean;
    isFetching: boolean;
    errors?: string[];
};

function buildInitialState(): AuthState {
    const storedAuthData = window.localStorage.getItem(LOCAL_STORAGE_AUTH_KEY);
    const storedRolePreference = window.localStorage.getItem(LOCAL_STORAGE_ROLE_PREF_KEY);

    let authData: AuthData | null = storedAuthData ? JSON.parse(storedAuthData) : null;
    const rolePreference: AuthDataRolesEnum | undefined = storedRolePreference
        ? JSON.parse(storedRolePreference)
        : undefined;
    let isLoggedIn = false;

    if (authData) {
        if (!isTokenExpired(authData)) {
            isLoggedIn = true;
        } else {
            console.log('session expired');
            clearAuthPersistence();
            authData = null;
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

const handleAuthResponse = createAsyncThunk<unknown, Promise<AuthData>, { dispatch: AppDispatch }>(
    'auth/handleAuthResponse',
    async (resp, { dispatch }) => {
        dispatch(setFetching(true));
        try {
            const data = await resp;
            console.log(data);

            // if (data.errors) {
            //     console.log(data.errors);
            //     dispatch(setAuthErrors(data.errors!));
            // }
            // else {
            persistAuthState(data);
            dispatch(setAuthData(data));
            // }
        } catch (err) {
            console.log('got err' + err);
            dispatch(setAuthErrors([JSON.stringify(err)])); // TODO error handling
        } finally {
            dispatch(setFetching(false));
        }
    }
);

export const login = createAsyncThunk<unknown, LoginData, { dispatch: AppDispatch; state: RootState }>(
    'auth/login',
    async (loginData, { getState, dispatch }) => {
        if (getState().auth.isFetching) return;

        const resp = getApis().authApi.login({ loginData });
        dispatch(handleAuthResponse(resp));
    }
);

export const logout = createAsyncThunk('auth/logout', (_, { dispatch }) => {
    clearAuthPersistence();
    dispatch(resetAuthData());
});

// TODO NOT USED, probably was a bad idea, leaving just in case
export const setRolePreference = createAsyncThunk<
    unknown,
    AuthDataRolesEnum,
    { dispatch: AppDispatch; state: RootState }
>('auth/roles/preference', (role: AuthDataRolesEnum, { getState, dispatch }) => {
    const auth = getState().auth;
    if (!auth || auth.rolePreference === role || !canHaveRolePreference(auth.authData, role)) return;

    localStorage.setItem(LOCAL_STORAGE_ROLE_PREF_KEY, JSON.stringify(role));
    dispatch(setRolePref(role));
});

export const authSlice = createSlice({
    name: 'auth',
    initialState: buildInitialState(),
    reducers: {
        setAuthData: (state: AuthState, action: PayloadAction<AuthData>) => {
            state.authData = action.payload;
            state.isLoggedIn = true;
            state.errors = undefined;
        },
        resetAuthData: (state: AuthState) => {
            state.authData = null;
            state.isLoggedIn = false;
            state.errors = undefined; // maybe leave it
        },
        setRolePref: (state: AuthState, action: PayloadAction<AuthDataRolesEnum>) => {
            state.rolePreference = action.payload;
        },
        clearAuthErrors: (state: AuthState) => {
            state.errors = undefined;
        },
        setAuthErrors: (state: AuthState, action: PayloadAction<string[]>) => {
            state.errors = action.payload;
        },
        setFetching: (state: AuthState, action: PayloadAction<boolean>) => {
            state.isFetching = action.payload;
        },
    },
});

const { setAuthData, resetAuthData, setAuthErrors, setFetching, setRolePref } = authSlice.actions;

export default authSlice.reducer;
