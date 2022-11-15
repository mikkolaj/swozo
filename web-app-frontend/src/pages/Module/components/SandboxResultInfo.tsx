import { Box, Divider, Typography } from '@mui/material';
import { ServiceModuleSandboxDto } from 'api';
import { LinkedTypography } from 'common/Styled/LinkedTypography';
import { stylesRow } from 'common/styles';
import { useTranslation } from 'react-i18next';
import { PageRoutes } from 'utils/routes';

type Props = {
    info: ServiceModuleSandboxDto;
};

export const SandboxResultInfo = ({ info }: Props) => {
    const { t } = useTranslation();
    console.log(info);
    return (
        <Box>
            <Typography variant="h5">{t('moduleSandbox.modal.result.success')}</Typography>
            <LinkedTypography
                sx={{ my: 2 }}
                decorated
                to={PageRoutes.Course(info.courseDetailsDto.id)}
                text={t('moduleSandbox.modal.result.courseLink')}
            />
            {info.sandboxStudents.length > 0 && (
                <Box>
                    <Typography sx={{ my: 2 }}>{t('moduleSandbox.modal.result.userData')}</Typography>
                    <Box>
                        {info.sandboxStudents.map(({ email, password }, idx) => (
                            <Box key={email}>
                                {idx > 0 && <Divider sx={{ my: 1, width: '80%' }} />}
                                <Box sx={{ ...stylesRow }}>
                                    <Typography>{t('moduleSandbox.modal.result.email')}</Typography>
                                    <Typography sx={{ fontWeight: '900', ml: 2 }}>{email}</Typography>
                                </Box>
                                <Box sx={{ ...stylesRow }}>
                                    <Typography>{t('moduleSandbox.modal.result.password')}</Typography>
                                    <Typography sx={{ fontWeight: '900', ml: 2 }}>{password}</Typography>
                                </Box>
                            </Box>
                        ))}
                    </Box>
                </Box>
            )}
            <Divider sx={{ my: 4 }} />
        </Box>
    );
};
