import { Box, Typography } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { ReadonlyField } from 'common/Input/Readonly/ReadonlyField';
import { InstructionView } from 'common/Styled/InstructionView';
import _ from 'lodash';
import { ComponentProps, PropsWithChildren } from 'react';
import { useTranslation } from 'react-i18next';

type Props = {
    serviceModule: ServiceModuleSummaryDto;
};

export const ServiceModuleGeneralInfo = ({ serviceModule, children }: PropsWithChildren<Props>) => {
    const { t } = useTranslation();

    return (
        <>
            <StyledReadonlyField
                wrapperSx={{ width: '50%' }}
                value={serviceModule.description}
                textFieldProps={{ multiline: true, fullWidth: true }}
                i18nLabel="myModule.description"
            />
            <StyledReadonlyField value={_.capitalize(serviceModule.subject)} i18nLabel="myModule.subject" />
            <StyledReadonlyField
                value={_.capitalize(serviceModule.serviceName)}
                i18nLabel="myModule.serviceName"
            />
            {children}
            <Box sx={{ m: 1 }}>
                <Typography>{t('myModule.teacherInstruction')}</Typography>
                <InstructionView
                    wrapperSx={{ maxWidth: '50%' }}
                    instruction={serviceModule.teacherInstruction}
                />
            </Box>
            <Box sx={{ m: 1 }}>
                <Typography>{t('myModule.studentInstruction')}</Typography>
                <InstructionView
                    wrapperSx={{ maxWidth: '50%' }}
                    instruction={serviceModule.studentInstruction}
                />
            </Box>
        </>
    );
};

export const StyledReadonlyField = ({ wrapperSx, ...props }: ComponentProps<typeof ReadonlyField>) => (
    <ReadonlyField wrapperSx={{ m: 1, ...wrapperSx }} {...props} />
);
