import DeleteIcon from '@mui/icons-material/Delete';
import DescriptionIcon from '@mui/icons-material/Description';
import { Box, Button, IconButton, Typography } from '@mui/material';
import { VmDto } from 'api';
import { getApis } from 'api/initialize-apis';
import { StackedList } from 'common/StackedList/StackedList';
import { StackedListContent } from 'common/StackedList/StackedListContent';
import { StackedListHeader } from 'common/StackedList/StackedListHeader';
import { InstructionView } from 'common/Styled/InstructionView';
import { stylesColumnCenteredHorizontal } from 'common/styles';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useMutation, useQueryClient } from 'react-query';
import { useDispatch } from 'react-redux';
import { toast } from 'react-toastify';
import { triggerError } from 'services/features/error/errorSlice';
import { CenteredModal } from 'services/features/modal/modals/CenteredModal';
import { ConfirmationRequiredModal } from 'services/features/modal/modals/ConfirmationRequiredModal';

type Props = {
    vms: VmDto[];
    onSwitchToCreator: () => void;
};

export const VirtualMachinesList = ({ vms, onSwitchToCreator }: Props) => {
    const { t } = useTranslation();
    const queryClient = useQueryClient();
    const dispatch = useDispatch();
    const [confirmationModalOpen, setConfirmationModalOpen] = useState(false);
    const [descriptionModalOpen, setDescriptionModalOpen] = useState(false);
    const [describedVm, setDescribedVm] = useState<VmDto>();
    const [vmToBeDeletedId, setVmToBeDeletedId] = useState(-1);

    const deleteVmMutation = useMutation((vmId: number) => getApis().vmApi.deleteVm({ id: vmId }), {
        onSuccess: (_, deletedVmId) => {
            queryClient.setQueryData(
                'vms',
                vms.filter((vm) => vm.id !== deletedVmId)
            );
            toast.success(t('toast.vmDeleted'));
        },
        onError: () => {
            queryClient.invalidateQueries('vms');
            dispatch(triggerError({ message: t('error.tryAgain') }));
        },
    });

    return (
        <Box>
            <StackedList
                /* eslint-disable react/jsx-key */
                header={
                    <StackedListHeader
                        proportions={[5, 2, 2, 3]}
                        items={['name', 'vcpu', 'ram', 'bandwidth'].map((label) => (
                            <Typography variant="body1" color="GrayText">
                                {t(`admin.vms.list.headers.${label}`)}
                            </Typography>
                        ))}
                    />
                }
                content={
                    <StackedListContent
                        proportions={[5, 2, 2, 2, 1]}
                        emptyItemsComponent={
                            <Box sx={{ ...stylesColumnCenteredHorizontal, justifyContent: 'center', mt: 8 }}>
                                <Typography
                                    sx={{
                                        overflowX: 'hidden',
                                        textOverflow: 'ellipsis',
                                        textAlign: 'center',
                                    }}
                                    variant="h4"
                                >
                                    {t('admin.vms.list.empty')}
                                </Typography>
                                <Button
                                    variant="contained"
                                    sx={{ mt: 4, px: 4, py: 2 }}
                                    onClick={() => onSwitchToCreator()}
                                >
                                    <Typography variant="h6">{t('admin.vms.list.emptyButton')}</Typography>
                                </Button>
                            </Box>
                        }
                        items={vms}
                        itemKeyExtractor={({ id }) => id}
                        itemRenderer={({ id, name, vcpu, ramGB, bandwidthMbps }) => [
                            <Typography variant="body1">{name}</Typography>,
                            <Typography variant="body1">{vcpu}</Typography>,
                            <Typography variant="body1">{ramGB}</Typography>,
                            <Typography variant="body1">{bandwidthMbps}</Typography>,
                            <Box sx={{ ml: 'auto' }}>
                                <IconButton
                                    onClick={() => {
                                        setDescribedVm(vms.filter((vm) => vm.id === id)[0]);
                                        setDescriptionModalOpen(true);
                                    }}
                                >
                                    <DescriptionIcon color="primary" />
                                </IconButton>
                                <IconButton
                                    onClick={() => {
                                        setVmToBeDeletedId(id);
                                        setConfirmationModalOpen(true);
                                    }}
                                    color="primary"
                                >
                                    <DeleteIcon />
                                </IconButton>
                            </Box>,
                        ]}
                    />
                }
                /* eslint-enable react/jsx-key */
            />
            <ConfirmationRequiredModal
                open={confirmationModalOpen}
                noText={t('admin.vms.list.delete.modal.no')}
                yesText={t('admin.vms.list.delete.modal.yes')}
                onClose={() => setConfirmationModalOpen(false)}
                onYes={() => {
                    setConfirmationModalOpen(false);
                    deleteVmMutation.mutate(vmToBeDeletedId);
                }}
                textLines={[t('admin.vms.list.delete.modal.question')]}
                yesPreffered={false}
            />
            <CenteredModal
                open={descriptionModalOpen}
                onClose={() => setDescriptionModalOpen(false)}
                modalSx={{ width: '50%' }}
            >
                <Box sx={{ p: 2 }}>
                    <Typography id="modal-modal-title" variant="h6" component="h2">
                        {t('admin.vms.list.details.label')}
                    </Typography>
                    {describedVm && (
                        <InstructionView
                            wrapperSx={{ boxShadow: 0 }}
                            instruction={{ untrustedPossiblyDangerousHtml: describedVm.descriptionHtml }}
                        />
                    )}
                </Box>
            </CenteredModal>
        </Box>
    );
};
