import { Box } from '@mui/material';
import { ServiceConfig } from 'api';
import { RichTextViewer } from 'common/Styled/RichTextViewer';
import { ScrollableCenteredModal } from 'common/Styled/ScrollableCenteredModal';
import _ from 'lodash';
import { useTranslation } from 'react-i18next';
import { getTranslated } from 'utils/util';

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
                name: _.capitalize(serviceConfig.serviceName),
            })}
            closeBtn={t('createModule.slides.0.form.help.closeBtn')}
        >
            <Box sx={{ mx: 4 }}>
                <RichTextViewer
                    untrustedPossiblyDangerousHtml={getTranslated(
                        i18n,
                        serviceConfig.configurationInstructionHtml
                    )}
                />
            </Box>
        </ScrollableCenteredModal>
    );
};
