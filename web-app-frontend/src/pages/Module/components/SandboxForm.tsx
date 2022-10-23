import { Box, Button } from '@mui/material';
import { FormInputField } from 'common/Input/FormInputField';
import { stylesRowCenteredVertical, stylesRowWithSpaceBetweenItems } from 'common/styles';
import { useTranslation } from 'react-i18next';

type Props = {
    buttonDisabled: boolean;
};

export const SandboxForm = ({ buttonDisabled }: Props) => {
    const { t } = useTranslation();
    return (
        <Box>
            <Box sx={{ ...stylesRowWithSpaceBetweenItems, px: 12 }}>
                <FormInputField
                    name="studentCount"
                    type="number"
                    i18nLabel="moduleSandbox.modal.setup.form.studentCount"
                />
                <FormInputField
                    name="validForMinutes"
                    type="number"
                    i18nLabel="moduleSandbox.modal.setup.form.validForMinutes"
                />
                <FormInputField
                    name="resultsValidForMinutes"
                    type="number"
                    i18nLabel="moduleSandbox.modal.setup.form.resultsValidForMinutes"
                />
            </Box>
            <Box
                sx={{
                    ...stylesRowCenteredVertical,
                    justifyContent: 'center',
                    mt: 4,
                }}
            >
                <Button
                    type="submit"
                    fullWidth
                    variant="contained"
                    disabled={buttonDisabled}
                    sx={{ width: '270px' }}
                >
                    {t('moduleSandbox.modal.setup.form.createButton')}
                </Button>
            </Box>
        </Box>
    );
};
