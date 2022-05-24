import { configureStore } from '@reduxjs/toolkit';
import { QueryClient } from 'react-query';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import authReducer from 'Services/features/auth/authSlice';

export const store = configureStore({
    reducer: {
        auth: authReducer,
    },
});

export const queryClient = new QueryClient();

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
export const useAppDispatch = () => useDispatch<AppDispatch>();

export const selectIsLoggedIn = (state: RootState) => state.auth.isLoggedIn;
