import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { AuthDetailsDto, AuthDetailsDtoRolesEnum, LoginRequest } from 'api';
import { getApis } from 'api/initialize-apis';
import { AppDispatch, RootState } from 'services/store';
import { hasRole } from 'utils/roles';
import { clearLocalStorage, loadFromLocalStorage, persistWithLocalStorage } from 'utils/util';

const LOCAL_STORAGE_AUTH_KEY = 'JWT';
const LOCAL_STORAGE_ROLE_PREF_KEY = 'ROLE_PREF';

export type AuthState = {
    authData: AuthDetailsDto | null;
    rolePreference?: AuthDetailsDtoRolesEnum;
    isLoggedIn: boolean;
    isFetching: boolean;
    errors?: string[];
};

export function isTokenExpired(authData: AuthDetailsDto) {
    return authData.expiresIn * 1000 <= new Date().getTime();
}

function canHaveRolePreference(authData: AuthDetailsDto | null, pref?: AuthDetailsDtoRolesEnum): boolean {
    if (!pref) return true;

    return hasRole(authData, pref);
}

function getDefaultRolePreference(authData: AuthDetailsDto): AuthDetailsDtoRolesEnum | undefined {
    if (hasRole(authData, AuthDetailsDtoRolesEnum.Teacher, AuthDetailsDtoRolesEnum.TechnicalTeacher))
        return AuthDetailsDtoRolesEnum.Teacher;
}

function buildInitialState(): AuthState {
    let authData = loadFromLocalStorage<AuthDetailsDto>(LOCAL_STORAGE_AUTH_KEY) ?? null;
    const rolePreference = loadFromLocalStorage<AuthDetailsDtoRolesEnum>(LOCAL_STORAGE_ROLE_PREF_KEY);
    let isLoggedIn = false;

    if (authData) {
        if (!isTokenExpired(authData)) {
            isLoggedIn = true;
        } else {
            console.log('session expired');
            clearLocalStorage(LOCAL_STORAGE_AUTH_KEY);
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

const handleAuthResponse = createAsyncThunk<unknown, Promise<AuthDetailsDto>, { dispatch: AppDispatch }>(
    'auth/handleAuthResponse',
    async (resp, { dispatch }) => {
        dispatch(setFetching(true));
        try {
            const data = await resp;
            // TODO error handling
            console.log(data);

            persistWithLocalStorage(LOCAL_STORAGE_AUTH_KEY, data);
            dispatch(receiveAuthData(data));

            let rolePreference = loadFromLocalStorage<AuthDetailsDtoRolesEnum>(LOCAL_STORAGE_ROLE_PREF_KEY);
            if (!rolePreference || !canHaveRolePreference(data, rolePreference)) {
                rolePreference = getDefaultRolePreference(data);
            }

            if (rolePreference) {
                dispatch(setRolePref(rolePreference));
            }
        } catch (err) {
            console.log('[LOGIN ERROR] ' + err);
            dispatch(setAuthErrors([JSON.stringify(err)])); // TODO error handling
        } finally {
            dispatch(setFetching(false));
        }
    }
);

export const login = createAsyncThunk<unknown, LoginRequest, { dispatch: AppDispatch; state: RootState }>(
    'auth/login',
    async (loginRequest, { getState, dispatch }) => {
        if (getState().auth.isFetching) return;

        const resp = getApis().authApi.login({ loginRequest });
        dispatch(handleAuthResponse(resp));
    }
);

export const logout = createAsyncThunk('auth/logout', (_, { dispatch }) => {
    clearLocalStorage(LOCAL_STORAGE_AUTH_KEY);
    dispatch(resetAuthData());
});

// TODO redirect if on page not-for-that-preference?
export const setRolePreference = createAsyncThunk<
    unknown,
    AuthDetailsDtoRolesEnum,
    { dispatch: AppDispatch; state: RootState }
>('auth/roles/preference', (role: AuthDetailsDtoRolesEnum, { getState, dispatch }) => {
    const auth = getState().auth;
    if (!auth || auth.rolePreference === role || !canHaveRolePreference(auth.authData, role)) return;

    persistWithLocalStorage(LOCAL_STORAGE_ROLE_PREF_KEY, role);
    dispatch(setRolePref(role));
});

export const authSlice = createSlice({
    name: 'auth',
    initialState: buildInitialState(),
    reducers: {
        receiveAuthData: (state: AuthState, action: PayloadAction<AuthDetailsDto>) => {
            state.authData = action.payload;
            state.isLoggedIn = true;
            state.errors = undefined;
        },
        resetAuthData: (state: AuthState) => {
            state.authData = null;
            state.isLoggedIn = false;
            state.errors = undefined; // maybe leave it
        },
        setRolePref: (state: AuthState, action: PayloadAction<AuthDetailsDtoRolesEnum>) => {
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

const { receiveAuthData, resetAuthData, setAuthErrors, setFetching, setRolePref } = authSlice.actions;

export default authSlice.reducer;
