import { createSlice, PayloadAction } from '@reduxjs/toolkit';

type ErrorOptions = {
    message: string;
    autoClose?: boolean;
};

export type ErrorState = {
    counter: number;
    shouldShow: boolean;
    options: ErrorOptions;
};

const initialState = (): ErrorState => ({
    counter: 0,
    shouldShow: false,
    options: {
        message: '',
        autoClose: true,
    },
});

// export const triggerError = createAsyncThunk<
//     unknown,
//     ErrorOptions,
//     { dispatch: AppDispatch; state: RootState }
// >('error/trigger', (options, { getState, dispatch }) => {
//     dispatch(set)
// });

export const errorSlice = createSlice({
    name: 'error',
    initialState: initialState(),
    reducers: {
        triggerError: (state: ErrorState, action: PayloadAction<ErrorOptions>) => {
            state.counter += 1;
            state.shouldShow = true;
            state.options = action.payload;
            state.options.autoClose = action.payload.autoClose ?? true;
        },
    },
});

export const { triggerError } = errorSlice.actions;

export default errorSlice.reducer;
