import { createSlice, PayloadAction } from '@reduxjs/toolkit';

export enum ModalId {
    MODULE_CREATION_IN_PROGRESS,
    REMIND_PASSWORD_IN_PROGRESS,
    REMIND_PASSWORD_EMAIL_SENT,
    SESSION_EXPIRED,
}

export type ModalProps = {
    id: ModalId;
    textLines?: string[];
    allowClose?: boolean;
};

type OpenModalOptions = {
    modalProps: ModalProps;
    forcePrecedence?: boolean;
};

export type ModalState = {
    modalQueue: ModalProps[];
};

const initialState = (): ModalState => ({
    modalQueue: [],
});

export const modalSlice = createSlice({
    name: 'modal',
    initialState: initialState(),
    reducers: {
        openModal: (state: ModalState, { payload }: PayloadAction<OpenModalOptions>) => {
            if (state.modalQueue.find((modal) => modal.id === payload.modalProps.id) !== undefined) {
                return;
            }

            if (payload.forcePrecedence) {
                state.modalQueue = [payload.modalProps, ...state.modalQueue];
            } else {
                state.modalQueue.push(payload.modalProps);
            }
        },
        closeModal: (state: ModalState, { payload }: PayloadAction<ModalId>) => {
            state.modalQueue = state.modalQueue.filter((modal) => modal.id !== payload);
        },
    },
});

export const { openModal, closeModal } = modalSlice.actions;

export default modalSlice.reducer;
