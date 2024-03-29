import { configureStore } from '@reduxjs/toolkit';
import { TypedUseSelectorHook, useDispatch, useSelector } from 'react-redux';
import authReducer from 'services/features/auth/authSlice';
import errorReducer from 'services/features/error/errorSlice';
import fileReducer from 'services/features/files/fileSlice';
import modalReducer from 'services/features/modal/modalSlice';

export const store = configureStore({
    reducer: {
        auth: authReducer,
        error: errorReducer,
        files: fileReducer,
        modal: modalReducer,
    },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
export const useAppDispatch = () => useDispatch<AppDispatch>();

export const selectIsLoggedIn = (state: RootState) => state.auth.isLoggedIn;
export const selectFileUploadState = (state: RootState, filename: string) => state.files.uploads[filename];
