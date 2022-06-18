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

function getPersistedAuthState(): AuthData | undefined {
    const data = window.localStorage.getItem(LOCAL_STORAGE_AUTH_KEY);
    return data ? JSON.parse(data) : undefined;
}

function getPersistedRolePreference(): AuthDataRolesEnum | undefined {
    const data = window.localStorage.getItem(LOCAL_STORAGE_ROLE_PREF_KEY);
    return data ? JSON.parse(data) : undefined;
}

// TODO im not sure if this role preference idea wont turn out too complicated
// its maily to distinguish between teacher and technical teacher views, we dont want to
// throw every option to normal teacher, but usually normal teacher will be also a technical teacher (and always vice versa)

// if we set rolePreference to teacher we can hide all these technical teacher buttons
function canHaveRolePreference(authData: AuthData | null, pref?: AuthDataRolesEnum): boolean {
    if (!pref) return true;

    return hasRole(authData, pref);
}

function getDefaultRolePreference(authData: AuthData): AuthDataRolesEnum | undefined {
    if (hasRole(authData, AuthDataRolesEnum.Teacher, AuthDataRolesEnum.TechnicalTeacher))
        return AuthDataRolesEnum.Teacher;
}

export type AuthState = {
    authData: AuthData | null;
    rolePreference?: AuthDataRolesEnum;
    isLoggedIn: boolean;
    isFetching: boolean;
    errors?: string[];
};

function buildInitialState(): AuthState {
    let authData = getPersistedAuthState() ?? null;
    const rolePreference = getPersistedRolePreference();
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
            // TODO error handling
            console.log(data);

            persistAuthState(data);
            dispatch(setAuthData(data));

            let rolePreference = getPersistedRolePreference();
            if (!rolePreference || !canHaveRolePreference(data, rolePreference)) {
                rolePreference = getDefaultRolePreference(data);
            }

            if (rolePreference) {
                dispatch(setRolePref(rolePreference));
            }
        } catch (err) {
            console.log('[LOGIN ERROR]' + err);
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

// TODO redirect if on page not-for-that-preference?
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