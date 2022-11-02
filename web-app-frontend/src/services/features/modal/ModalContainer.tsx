import { useEffect, useState } from 'react';
import { useAppDispatch, useAppSelector } from 'services/store';
import { LoadingModal } from './modals/LoadingModal';
import { SuccessModal } from './modals/SuccessModal';
import { closeModal, ModalId } from './modalSlice';

export const ModalContainer = () => {
    const modal = useAppSelector((state) =>
        state.modal.modalQueue.length > 0 ? state.modal.modalQueue[0] : undefined
    );
    const dispatch = useAppDispatch();
    const [modalComponent, setModalComponent] = useState<JSX.Element>();

    useEffect(() => {
        const establishModalComponent = () => {
            if (!modal) return undefined;
            const onClose = modal.allowClose ? () => dispatch(closeModal(modal.id)) : () => undefined;

            switch (modal.id) {
                case ModalId.MODULE_CREATION_IN_PROGRESS:
                case ModalId.REMIND_PASSWORD_IN_PROGRESS:
                    return <LoadingModal textLines={modal.textLines ?? []} onClose={onClose} />;
                case ModalId.REMIND_PASSWORD_EMAIL_SENT:
                    return <SuccessModal textLines={modal.textLines ?? []} onClose={onClose} />;
            }
        };

        setModalComponent(establishModalComponent());
    }, [modal, dispatch]);

    return <>{modalComponent}</>;
};
