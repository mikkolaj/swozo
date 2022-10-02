import { MenuItem } from '@mui/material';
import { FormSelectField } from 'common/Input/FormSelectField';
import { SlideProps } from 'common/SlideForm/util';

export const ModuleSpecsForm = ({ nameBuilder }: SlideProps) => {
    return (
        <>
            {/* This should be dynamic, from server */}
            <FormSelectField
                name={nameBuilder('environment')}
                i18nLabel="createModule.slides.1.form.environment"
            >
                <MenuItem value={'isolated'}>Izolowane</MenuItem>
            </FormSelectField>
            <FormSelectField name={nameBuilder('storage')} i18nLabel="createModule.slides.1.form.storage">
                <MenuItem value={1}>1GB</MenuItem>
            </FormSelectField>
            <FormSelectField name={nameBuilder('cpu')} i18nLabel="createModule.slides.1.form.cpu">
                <MenuItem value={'big'}>Duże</MenuItem>
            </FormSelectField>
            <FormSelectField name={nameBuilder('ram')} i18nLabel="createModule.slides.1.form.ram">
                <MenuItem value={'big'}>Duże</MenuItem>
            </FormSelectField>
        </>
    );
};
