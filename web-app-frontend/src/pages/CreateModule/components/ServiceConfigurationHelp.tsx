import { Box } from '@mui/material';
import { ServiceConfig } from 'api';
import { ScrollableCenteredModal } from 'common/Styled/ScrollableCenteredModal';
import { useTranslation } from 'react-i18next';
import { getTranslated } from 'utils/util';
import './configHelp.css';

type Props = {
    open: boolean;
    onClose: () => void;
    serviceConfig: ServiceConfig;
};

export const ServiceConfigurationHelp = ({ onClose, open, serviceConfig }: Props) => {
    const { t, i18n } = useTranslation();
    return (
        <ScrollableCenteredModal
            open={open}
            onClose={onClose}
            header={t('createModule.slides.0.form.help.header', {
                name: serviceConfig.displayName,
            })}
            closeBtn={t('createModule.slides.0.form.help.closeBtn')}
        >
            <Box sx={{ mx: 4 }}>
                <span
                    dangerouslySetInnerHTML={{
                        __html: getTranslated(i18n, serviceConfig.configurationInstructionHtml),
                    }}
                />
            </Box>
        </ScrollableCenteredModal>
    );
};
