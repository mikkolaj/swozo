import { Box, Button, Typography } from '@mui/material';
import { ServiceModuleSummaryDto } from 'api';
import { ReadonlyField } from 'common/Input/Readonly/ReadonlyField';
import { InstructionView } from 'common/Styled/InstructionView';
import { stylesRow } from 'common/styles';
import _ from 'lodash';
import { ComponentProps, PropsWithChildren } from 'react';
import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import { PageRoutes } from 'utils/routes';

type Props = {
    serviceModule: ServiceModuleSummaryDto;
    descriptionWidth?: string;
};

export const ServiceModuleGeneralInfo = ({
    serviceModule,
    descriptionWidth,
    children,
}: PropsWithChildren<Props>) => {
    const { t } = useTranslation();

    return (
        <>
            <StyledReadonlyField
                wrapperSx={{ width: descriptionWidth ?? '50%' }}
                value={serviceModule.description}
                textFieldProps={{ multiline: true, fullWidth: true }}
                i18nLabel="myModule.description"
            />
            <StyledReadonlyField value={_.capitalize(serviceModule.subject)} i18nLabel="myModule.subject" />
            <Box sx={{ ...stylesRow }}>
                <StyledReadonlyField value={serviceModule.serviceName} i18nLabel="myModule.serviceName" />
                <Button variant="text">
                    <Link
                        target="_blank"
                        rel="noopener"
                        to={PageRoutes.Service(serviceModule.serviceName)}
                        style={{ textDecoration: 'none', color: 'inherit' }}
                    >
                        {t('myModule.seeService')}
                    </Link>
                </Button>
            </Box>
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
