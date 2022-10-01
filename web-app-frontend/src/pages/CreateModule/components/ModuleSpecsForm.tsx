import { MenuItem } from '@mui/material';
import { SlideFormSelectField } from 'common/SlideForm/SlideFormSelectField';
import { SlideProps } from 'common/SlideForm/util';

export const ModuleSpecsForm = ({ nameBuilder }: SlideProps) => {
    return (
        <>
            {/* This should be dynamic, from server */}
            <SlideFormSelectField
                name={nameBuilder('environment')}
                i18nLabel="createModule.slides.1.form.environment"
            >
                <MenuItem value={'isolated'}>Izolowane</MenuItem>
            </SlideFormSelectField>
            <SlideFormSelectField
                name={nameBuilder('storage')}
                i18nLabel="createModule.slides.1.form.storage"
            >
                <MenuItem value={1}>1GB</MenuItem>
            </SlideFormSelectField>
            <SlideFormSelectField name={nameBuilder('cpu')} i18nLabel="createModule.slides.1.form.cpu">
                <MenuItem value={'big'}>Duże</MenuItem>
            </SlideFormSelectField>
            <SlideFormSelectField name={nameBuilder('ram')} i18nLabel="createModule.slides.1.form.ram">
                <MenuItem value={'big'}>Duże</MenuItem>
            </SlideFormSelectField>
        </>
    );
};
